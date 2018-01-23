-module(server).
-export([server/3]).

%===========
% Server Configs : initialize erlzmq context, and bind a socket for accept connections.
server(Port, N, Broker) ->
    spawn(fun() -> login:start() end),
    spawn(fun() -> exchangeManager:start() end),
    createConsumers(N, Broker),
    process_flag(trap_exit, true),
    {ok, LSock} = gen_tcp:listen(Port, [binary, {packet, 0}, {active, false}]),
    acceptor(LSock).

%===========
% process runing for accept new connects
acceptor(LSock) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    spawn(fun() -> acceptor(LSock) end),
    waitLogin(Sock).

%============
% Receive all message and process them. The message are decoded/encoded by ProtocolBuffers.
waitLogin(Sock) ->
    case gen_tcp:recv(Sock,0) of
        {ok, Data} ->
            Map = protos:decode_msg(Data,'MsgCS'),
            case maps:find(type, Map) of
                {ok,"1"} ->
                    Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "User&Pass"}}, 'MsgCS'),
                    gen_tcp:send(Sock, Bin),
                    waitLogin(Sock);
                {ok,"2"} ->
                    {ok, UserInfo} = maps:find(info, Map),
                    {ok, User} = maps:find(user, UserInfo),
                    {ok, Pass} = maps:find(pass, UserInfo),
                    case login:login(User, Pass) of                
                        logged ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "Logged"}}, 'MsgCS'),
                            gen_tcp:send(Sock, Bin),
                            user:run(Sock, User);
                        invalid ->
                            Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => false, msg => "Invalid"}}, 'MsgCS'),
                            gen_tcp:send(Sock, Bin),
                            waitLogin(Sock);
                        _ -> 
                            io:format("Error.\n")
                    end;
                _ ->
                    io:format("Invalid\n"),
                    waitLogin(Sock)
            end;
        {error, closed} ->
            io:format("User closed\n")
    end.
   
%============
% Create N Consumers
createConsumers(1, Port) ->
    spawn(fun() -> consumer:run(Port) end);
createConsumers(N, Port) ->
    spawn(fun() -> consumer:run(Port) end),
    createConsumers(N-1, Port).
 
