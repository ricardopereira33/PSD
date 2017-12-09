-module(chat).
-export([server/1]).

%===========
% Server settings
server(Port) ->
    LoginManager = spawn(fun() -> login_manager:loop(#{}) end),
    {ok, LSock} = gen_tcp:listen(Port, [list, {packet, line}, {reuseaddr, true}]),
    acceptor(LSock, LoginManager).

%===========
% process runing for accpet new connects
acceptor(LSock, LoginManager) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock, LoginManager) end),
    login(Sock, LoginManager).


%===========
% Login implemention
login(Sock, LoginManager) ->
    printLogin(Sock),
    receive
        {tcp, _, Op} ->
            case string:tokens(Op, "\n\t\r ") of
                ["1"] ->
                    {User, _Pass} = opLogin(Sock, LoginManager),
                    receive
                        {_, logged} ->
                            gen_tcp:send(Sock, "\nLogged in!\n"),
                            RoomManager ! {enter, RoomName, self()},
                            user(Sock, User, LoginManager);
                        {_, invalid} ->
                            gen_tcp:send(Sock, "\nUsername or password incorrect!\n\n"),
                            login(Sock, LoginManager)
                    end;
                ["2"] ->
                    opRegister(Sock, LoginManager),
                    receive
                        {_, created} ->
                            gen_tcp:send(Sock, "\nAccount created!\n\n"),
                            login(Sock, LoginManager);
                        {_, user_exists} ->
                            gen_tcp:send(Sock, "\nUsername already used!\n\n"),
                            login(Sock, LoginManager)
                    end;
                ["3"] ->
                    gen_tcp:send(Sock, "Adeus! :D\n");
                _ ->
                    gen_tcp:send(Sock, "\nInvalid Option!\n\n"),
                    login(Sock, LoginManager)
            end
    end.


% login operation
opLogin(Sock, LoginManager) ->
    gen_tcp:send(Sock, "Username: "),
    receive
        {tcp, _, Username} ->
            [User] = string:tokens(Username, "\n\t\r ")
    end,
    gen_tcp:send(Sock, "Password: "),
    receive
        {tcp, _, Password} ->
            [Pass] = string:tokens(Password, "\n\t\r ")
        end,
    LoginManager ! {login, User, Pass, self()},
    {User, Pass}.


% register operation
opRegister(Sock, LoginManager) ->
    gen_tcp:send(Sock, "Username: "),
    receive
        {tcp, _, Username} ->
            [User] = string:tokens(Username, "\n\t\r ")
    end,
    gen_tcp:send(Sock, "Password: "),
    receive
        {tcp, _, Password} ->
            [Pass] = string:tokens(Password, "\n\t\r ")
    end,
    LoginManager ! {create_account, User, Pass, self()}.


% print Menu 
printLogin(Sock) ->
    gen_tcp:send(Sock,"======= Chat =======\n"),
    gen_tcp:send(Sock,"1 - Login\n"),
    gen_tcp:send(Sock,"2 - Register\n"),
    gen_tcp:send(Sock,"3 - Exit\n"),
    gen_tcp:send(Sock,"====================\n").

%===========
% RoomManager implemention
roomManager(RoomManager) ->
    receive
        {changeRoom, OldRoom, NewRoom, User} ->
            OldRoom ! {leave, User},
            case maps:find(NewRoom, RoomManager) of
                {ok, {Num, Room}} ->
                    Room ! {enter, User},
                    User ! {ok, Room},
                    roomManager(maps:update(NewRoom, {Num+1, Room}, RoomManager));
                error ->
                    CreatRoom = spawn (fun() -> room([]) end),
                    CreatRoom ! {enter, User},
                    User ! {ok, CreatRoom},
                    roomManager(maps:put(NewRoom, {1, CreatRoom}, RoomManager))
            end;
        {enter, RoomName, User} ->
            case maps:find(RoomName, RoomManager) of
                {ok, {Num, Room}} ->
                    Room ! {enter, User},
                    roomManager(maps:update(RoomName, {Num+1, Room}, RoomManager))
            end;
        {leave, RoomName, User} ->
            case maps:find(RoomName, RoomManager) of
                {ok, {1, Room}} ->
                    Room ! {leave, User},
                    roomManager(maps:remove(RoomName, RoomManager));
                {ok, {Num, Room}} ->           
                    Room ! {leave, User},
                    roomManager(maps:update(RoomName, {Num-1, Room}, RoomManager))
            end
    end.

%===========
% Room implemention
room(Pids) ->
    receive
        {enter, Pid} ->
            io:format("user entered~n", []),
            room([Pid | Pids]);
        {line, Data, User} ->
            io:format("received ~p~n", [Data]),
            [Pid ! {line,Data} || Pid <- Pids, Pid =/= User],
            room(Pids);
        {online, List} ->
            io:format("Online users: ~p",[List]),
            room(Pids);
        {leave, Pid} ->
            io:format("user left~n", []),
            if
                length(Pids) > 1 -> 
                    room(Pids -- [Pid])
            end
    end.

%===========
% User implemention
user(Sock, Room, RoomName, RoomManager, User, LoginManager) ->
    receive
        {line, Data} ->
            gen_tcp:send(Sock, Data),
            user(Sock, Room, RoomName, RoomManager, User, LoginManager);
        {tcp, _, Data} ->
            case Data of
                "\\room " ++ T ->
                    RoomManager ! {changeRoom, Room, T, self()},
                    receive
                        {ok, NewRoom} ->
                            user(Sock, NewRoom, T, RoomManager, User, LoginManager)
                    end;
                "\\online" ++ _T ->
                    LoginManager ! {online, self()},
                    receive
                        {_, List} -> 
                            gen_tcp:send(Sock, "Online List: " ++ toString(List)),
                            user(Sock, Room, RoomName, RoomManager, User, LoginManager)
                    end;
                _ ->
                    Room ! {line, User ++":\t"++ Data, self()},
                    user(Sock, Room, RoomName, RoomManager, User, LoginManager)
            end;
        {tcp_closed, _} ->
            RoomManager ! {leave, RoomName, self()};
        {tcp_error, _, _} ->
            RoomManager ! {leave, RoomName, self()}
    end.

% print users list 
toString([H])   -> H ++ ".\n";
toString([H|T]) -> H ++ ", " ++ toString(T).
