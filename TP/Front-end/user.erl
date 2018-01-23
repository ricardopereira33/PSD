-module(user).
-export([run/2]).

%===========
% run : process send orders to Exchange
run(Sock, User) ->
    Pid = self(),
    spawn(fun() -> worker(Sock, User, Pid) end),
    receiver(Sock, User).

% receive 
receiver(Sock, User) ->
    io:format("Entrei\n"),
    receive
        {msg, Data} ->
            io:format("Recebi\n"),
            Map = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok,"3"} -> 
                    {ok, MapOrder} = maps:find(orderRequest, Map),
                    {ok, Company} = maps:find(company_id, MapOrder),
                    io:format("Sending.\n"),
                    Pid = exchangeManager:getExchange(Company),
                    producer:order(Data, Pid),
                    Bin = protos:encode_msg(#{type=>"4"}, 'MsgCS'),
                    gen_tcp:send(Sock, Bin),
                    run(Sock, User);
                _ ->
                    io:format("Pack invalid"),
                    run(Sock, User)
            end;
        {transaction, Data} ->
            gen_tcp:send(Sock, Data),
            run(Sock, User);
        _ ->
            io:format("Error.\n")
    end,
    io:format("End.").

% ator - tpc connections 
worker(Sock, User, Pid) ->
    io:format("fds\n"),
    case gen_tcp:recv(Sock, 0) of
        {ok, Data} ->
            io:format("nice\n"),
            Pid ! {msg, Data},
            worker(Sock, User, Pid);
        {error, closed} ->
            login:logout(User),
            io:format("User closed\n")
    end.