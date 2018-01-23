-module(consumer).
-export([run/1]).

run(Port) -> 
    {ok, Ctx} = erlzmq:context(1),
    {ok, Sock} = erlzmq:socket(Ctx, [pull, {active, false}]),
    ok = erlzmq:connect(Sock, "tcp://localhost:" ++ integer_to_list(Port)),
    loop(Sock).

loop(Sock) -> 
    case erlzmq:recv(Sock) of 
        {ok, Data} ->   
            Msg = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Msg) of
                {ok, "4"} -> 
                    {ok, MapOrder} = maps:find(orderReply, Msg),
                    {ok, User} = maps:find(user, MapOrder),
                    {ok, Notfication} = maps:find(notification, MapOrder),
                    Pid = login:request_pid(User),
                    Pid ! {transaction, Data},
                    loop(Sock);
                _ ->
                    io:format("Error.\n"),
                    loop(Sock)
            end;        
        {error, closed} ->
            loop(Sock);
        _ -> 
            io:format("Erro\n")
    end.
