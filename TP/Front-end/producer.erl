-module(producer).
-export([run/1, order/2]).

run(Addr) -> 
    {ok, Ctx} = erlzmq:context(),
    {ok, Sock} = erlzmq:socket(Ctx, [push, {active, false}]),
    ok = erlzmq:connect(Sock, Addr),
    io:format("Addr: ~p",[Addr]),
    loop(Sock).

order(Data, Pid) ->
    Pid ! {new_order, Data, self()},
    receiveMsg().

receiveMsg() ->
    receive
        {_, Res} -> Res
    end.

loop(Sock) ->
    receive
        {new_order, Data, From} ->
            erlzmq:send(Sock, Data),
            From ! {self(), ok},
            loop(Sock)
    end.
    
