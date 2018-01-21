-module(server).
-export([server/2]).

%=================
% Server Configs : initialize erlzmq context, and bind a socket for accept connections.
server(Port, Broker) ->
    LoginManager = spawn(fun()-> loginManager(#{"user1" => {"33", false},"user2" => {"85", false},"user3" => {"3", false}}) end),
    process_flag(trap_exit, true),
    {ok, LSock} = gen_tcp:listen(Port, [binary, {packet, 0}, {active, false}]),
    acceptor(LSock, LoginManager, Broker).

%===========
% process runing for accpet new connects
acceptor(LSock, LoginManager, Broker) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock, LoginManager, Broker) end),
    waitLogin(LoginManager, Sock, Broker).


%================================
% Receive all message and process them. The message are decoded/encoded by ProcolBuffers.
waitLogin(LoginManager, Sock, Broker) ->
    case gen_tcp:recv(Sock,0) of
        {ok, Data} ->
            Map = protos:decode_msg(Data,'MsgCS'),
            case maps:find(type, Map) of
                {ok,"1"} ->
                    Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "User&Pass"}}, 'MsgCS'),
                    gen_tcp:send(Sock, Bin),
                    waitLogin(LoginManager, Sock, Broker);
                {ok,"2"} ->
                    {ok, MapUser} = maps:find(info, Map),
                    {ok, User} = maps:find(user, MapUser),
                    {ok, Pass} = maps:find(pass, MapUser),
                    LoginManager ! {login, User, Pass, self()},
                    receive
                        {_, logged} ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "Logged"}}, 'MsgCS'),
                            gen_tcp:send(Sock, Bin),
                            {ok, Ctx} = erlzmq:context(),
                            {ok, ZmqSock} = erlzmq:socket(Ctx, [pub, {active, false}]),
                            ok = erlzmq:connect(ZmqSock, "tcp://localhost:"++integer_to_list(Broker)),
                            user(Sock, User, LoginManager, ZmqSock);
                        {_, invalid} ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => false, msg => "Invalid"}}, 'MsgCS'),
                            gen_tcp:send(Sock, Bin),
                            waitLogin(LoginManager, Sock, Broker)
                    end;
                _ ->
                    io:format("Invalid\n"),
                    waitLogin(LoginManager, Sock, Broker)
            end;
        {error, closed} ->
            io:format("User closed\n")
    end.

%===========
% Login Manager : manage all users in system
loginManager(M) ->
    receive
        {login, U, P, From} ->
            case maps:find(U, M) of
                {ok,{P, false}} ->
                    From ! {self(), logged},
                    io:format("account ~p logged in\n", [U]),
                    loginManager(maps:update(U,{P,true}, M));
                _ -> 
                    From ! {self(), invalid},
                    loginManager(M)
            end;
        {logout, U, From} -> 
            From ! {self(), ok},
            io:format("account ~p logged out\n", [U]),
            {P,_} = maps:get(U, M),
            loginManager(maps:update(U, {P,false}, M))
    end.


%===========
% User : process send orders to Exchange
user(Sock, User, LoginManager, ZmqSock) ->
    case gen_tcp:recv(Sock, 0) of 
        {ok, Data} ->
            Map = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok,"3"} -> 
                    {ok, MapOrder} = maps:find(orderRequest, Map),
                    {ok, Type} = maps:find(type, MapOrder),
                    case string:tokens(Type, "\n\t\r ") of
                        ["1"] ->
                            io:format("Send sell\n"),
                            erlzmq:send(ZmqSock, Data);
                        ["2"] ->
                            io:format("Send buy\n"),
                            erlzmq:send(ZmqSock, Data)
                    end,
                    Bin = protos:encode_msg(#{type=>"4"}, 'MsgCS'),
                    gen_tcp:send(Sock, Bin),
                    user(Sock, User, LoginManager, ZmqSock);
                _ ->
                    io:format("Pack invalid"),
                    user(Sock, User, LoginManager, ZmqSock)
            end;
        {error, closed} ->
            LoginManager ! {logout, User, self()},
            io:format("User closed\n")
    end.
