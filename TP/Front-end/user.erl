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
    receive
        {msg, Data} ->
            Map = protos:decode_msg(Data, 'MsgCS'),
            case maps:find(type, Map) of
                {ok,"3"} -> 
                    {ok, MapOrder} = maps:find(orderRequest, Map),
                    {ok, Company} = maps:find(company_id, MapOrder),
                    Pid = exchangeManager:getExchange(Company),
                    ok = producer:order(Data, Pid),
                    Bin = newOrderMsg(User),
                    gen_tcp:send(Sock, Bin),
                    receiver(Sock, User);
                _ ->
                    io:format("Pack invalid"),
                    receiver(Sock, User)
            end;
        {msgError, _} ->
            Bin = newMsgClosed(User),
            gen_tcp:send(Sock, Bin),
            receiver(Sock, User);
        {transaction, Data} ->
            gen_tcp:send(Sock, Data),
            receiver(Sock, User);
        _ ->
            io:format("Error.\n")
    end.

% ator - tcp connections 
worker(Sock, User, Pid) ->
    case gen_tcp:recv(Sock, 0) of
        {ok, Data} ->
            IsValid = checkHour(), 
            if 
                IsValid ->  Pid ! {msg, Data};
                true -> Pid ! {msgError, Data}
            end,
            worker(Sock, User, Pid);
        {error, closed} ->
            login:logout(User),
            io:format("User closed\n")
    end.

newOrderMsg(User) ->
    protos:encode_msg(#{type=>"4", orderReply => #{ user => User, notification => "Order Registered."}}, 'MsgCS').

newMsgClosed(User) ->
    protos:encode_msg(#{type=>"4", orderReply => #{ user => User, notification => "Is closed."}}, 'MsgCS').

checkHour() ->
    Hour = element(1,time()),
    if
        (Hour < 17) and (Hour >= 9) -> true;
        true -> false
    end.