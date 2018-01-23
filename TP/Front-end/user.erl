-module(user).
-export([run/2]).

%===========
% run : process send orders to Exchange
run(Sock, User) ->
    receive
        {tcp, Sock, Data} ->
            Map = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok,"3"} -> 
                    {ok, MapOrder} = maps:find(orderRequest, Map),
                    {ok, Company} = maps:find(company_id, MapOrder),
                    Pid = exchangeManager:getExchange(Company),
                    producer:order(Data, Pid),
                    Bin = protos:encode_msg(#{type=>"4"}, 'MsgCS'),
                    gen_tcp:send(Sock, Bin),
                    run(Sock, User, ZmqSock);
                _ ->
                    io:format("Pack invalid"),
                    run(Sock, User, ZmqSock)
            end;
        {transaction, Data} ->
            gen_tcp:send(Sock, Data),
            run(Sock, User, ZmqSock);
        {tcp_closed, _} ->
            login:logout(User),
            io:format("User closed\n");
        {tcp_error, _} ->
            login:logout(User),
            io:format("User error\n")
    end.