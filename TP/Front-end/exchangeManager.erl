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
                    {ExPid, NewMap} = putExchange(MapExchanges, Ex, Addr),
                    NewCache = putCache(Cache, Company, ExPid),
                    From ! {?MODULE, ExPid},
                    run(NewMap, NewCache)
            end
    end.

requestDir(Company) ->
    inets:start(),
    URL = "http://localhost:8080/company/" ++ Company,
    case httpc:request(URL) of
        {ok, {_,_,Res}} -> 
            inets:stop(),
            {struct, MapResult} = mochijson:decode(Res),
            Host = keyfind("host", length(MapResult), MapResult),
            Port = keyfind("port", length(MapResult), MapResult),
            Exchange = keyfind("exchange", length(MapResult), MapResult),
            {Exchange,Host++""++Port}
    end.

putExchange(MapExchange, Exchange, Addr) -> 
    case maps:find(Exchange, MapExchange) of
        {ok, ExchangePush} -> 
            {ExchangePush, MapExchange};
        _ ->
            NewExchangePush = spawn(fun() -> producer:run(Addr) end),
            NewMap = maps:put(Exchange, NewExchangePush, MapExchange),
            {NewExchangePush, NewMap}
    end.

putCache(Cache, Company, ExchangePid) ->
    maps:put(Company, ExchangePid, Cache).


keyfind(_, 0, _) -> 
    false;
keyfind(Key, N, [{KeyL, ValueL}|T]) -> 
    if 
        Key =:= KeyL -> ValueL;
        true -> keyfind(Key,N,T)
    end.
