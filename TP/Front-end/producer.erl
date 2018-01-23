-module(producer).
-export([run/1, order/2]).

run(Addr) -> 
    {ok, Ctx} = erlzmq:context(),
    {ok, Sock} = erlzmq:socket(Ctx, [push, {active, false}]),
    ok = erlzmq:connect(Sock, Addr),
    spawn(fun() -> loop(Sock) end).

order(Data, Pid) ->
    Pid ! {new_order, Data, From},
    receiveMsg().

receiveMsg() ->
    receive
        {Pid, Res} -> Res
    end.

loop(Sock) ->
    receive
        {new_order, Data, From} ->
            erlzmq:send(Sock, Data),
            From ! {Pid, ok}
    end.
    
