-module(consumer).
-export([run/1]).

run(Port) -> 
    {ok, Ctx} = erlzmq:context(),
    {ok, Sock} = erlzmq:socket(Ctx, [pull, {active, false}]),
    ok = erlzmq:connect(Sock, "tcp://localhost:" ++ integer_to_list(Port)),
    loop(Sock).


loop(Sock) -> 
    case erlzmq:recv(Sock) of 
        {ok, Data} ->   
            Msg = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok, "4"} -> 
                    {ok, User} = maps:find(user, Msg),
                    {ok, Notfication} = maps:find(notification, Msg),
                    Pid = login:request_pid(User),
                    Pid ! {transation, Notfication},
                    loop();
                _ ->
                    io:format("Error.\n"),
                    loop()
            end;        
        {error, closed} ->
            loop().
    end.
