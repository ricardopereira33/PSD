-module(server).
-export([server/0]).

server() ->
    LoginManager = spawn(fun()-> loginManager(#{"ri" => {"33", false}}) end),
    process_flag(trap_exit, true),
    {ok, Ctx} = erlzmq:context(),
    {ok, Sock} = erlzmq:socket(Ctx, [rep, {active, false}]),
    ok = erlzmq:bind(Sock, "tcp://*:3333"),
    io:format("Accept connection\n"),
    waitLogin(LoginManager, Sock).


waitLogin(LoginManager, Sock) ->
    case erlzmq:recv(Sock) of
        {ok, Data} ->
            io:format("Data: ~p\n",[Data]),
            Map = protos:decode_msg(Data,'MsgCS'),
            case maps:find(type, Map) of
                {ok,"1"} ->
                    io:format("Type1\n"),
                    Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "User&Pass"}}, 'MsgCS'),
                    erlzmq:send(Sock, Bin),
                    waitLogin(LoginManager, Sock);
                {ok,"2"} ->
                    io:format("Type2\n"),
                    {ok, MapUser} = maps:find(info, Map),
                    {ok, User} = maps:find(user, MapUser),
                    {ok, Pass} = maps:find(pass, MapUser),
                    LoginManager ! {login, User, Pass, self()},
                    receive
                        {LoginManager, logged} ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "Logged"}}, 'MsgCS'),
                            erlzmq:send(Sock,Bin),
                            user(Sock, User, LoginManager);
                        {LoginManager, invalid} ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => false, msg => "Invalid"}}, 'MsgCS'),
                            erlzmq:send(Sock,Bin),
                            waitLogin(LoginManager, Sock)
                    end;
                _ ->
                    io:format("Pack invalid"),
                    waitLogin(LoginManager, Sock)
            end;
        {error, closed} ->
            io:format("User closed")
    end.

loginManager(M) ->
    receive
        {login, U, P, From} ->
            case maps:find(U, M) of
                {ok,{P, false}} ->
                    From ! {self(), logged},
                    io:format("account ~p logged in~n", [U]),
                    loginManager(maps:update(U,{P,true}, M));
                _ -> 
                    From ! {self(), invalid},
                    loginManager(M)
            end;
        {logout, U, From} -> 
            From ! {self(), ok},
            io:format("account ~p logged out~n", [U]),
            {P,_} = maps:get(U, M),
            loginManager(maps:update(U, {P,false}, M))
    end.


user(Sock, Username, LoginManager) ->
    case erlzmq:recv(Sock) of 
        {ok, Data} ->
            Map = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok,"3"} -> 
                    {ok, MapOrder} = maps:find(order, Map),
                    {ok, Type} = maps:find(type, MapOrder),
                    case string:tokens(Type, "\n\t\r ") of
                        ["1"] ->
                            io:format("Order1\n");
                        ["2"] ->
                            io:format("Order2\n")
                    end,
                    user(Sock, Username, LoginManager);
                _ ->
                    io:format("Pack invalid"),
                    user(Sock, Username, LoginManager)
            end;
        {error, closed} ->
            io:format("User closed")
    end.

