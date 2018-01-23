-module(exchangeManager).
-export([start/0, getExchange/1]).

start() ->
    register(?MODULE, spawn( fun() -> run(#{}, #{}) end)).

getExchange(Company) ->
    ?MODULE ! { getExchange, Company, self()},
    receiveMsg().

receiveMsg() ->
    receive
        {?MODULE, Res} -> Res
    end.

run(MapExchanges, Cache) -> 
    receive
        {getExchange, Company, From} -> 
            case maps:find(Company, Cache) of
                {ok, Pid} ->
                    From ! {?MODULE, Pid},
                    run(MapExchanges, Cache);
                _ -> 
                    {Ex, Addr} = requestDir(Company),
                    ExPid = putExchange(MapExchanges, Ex, Addr),
                    putCache(Cache, Company, ExPid),
                    From ! {?MODULE, ExPid},
                    run(MapExchanges, Cache)
            end
    end.

requestDir(Company) ->
    inets:start(),
    httpc:set_options([port, 8080]),
    httpc:resquest(get, {"localhost/Company/"++Company}),
    receive
        {http, {_, Json}} -> 
            inets:stop(),
            Result = jsone:decode(Json),
            io:format("map: ~p",[Result])
    end.

putExchange(MapExchange, Exchange, Addr) -> 
    case maps:find(Exchange) of
        {ok, ExchangePush} -> 
            ExchangePush;
        _ ->
            NewExchangePush = spawn(fun() -> producer:run(Addr) end),
            maps:put(Exchange, NewExchangePush, MapExchange),
            NewExchangePush
    end.

putCache(Cache, Company, ExchangePid) ->
    maps:put(Company, ExchangePid, Cache).

