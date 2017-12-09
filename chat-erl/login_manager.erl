-module(login_manager).
-export([start/0, loop/1, create_account/2, close_account/2, login/2, logout/1, online/0]).

% spawn cria um processo, que corre o loop neste casso.
start() ->
    register(?MODULE, spawn( fun() -> loop(#{}) end) ).

create_account(User, Pass) ->
    ?MODULE ! { create_account, User, Pass, self()},
    receive
        {?MODULE, Res} ->
            Res
    end.

close_account(User, Pass) ->
    ?MODULE ! { close_account, User, Pass, self()},
    receive
        {?MODULE, Res} ->
            Res
    end.

login(User, Pass) ->
    ?MODULE ! { login, User, Pass, self()},
    receive
        {?MODULE, Res} ->
            Res
    end.

logout(User) ->
    ?MODULE ! { logout, User, self()},
    receive
        {?MODULE, Res} ->
            Res
    end.

online() ->
    ?MODULE ! { online, self()},
    receive
        {?MODULE, Res} ->
            Res
    end.

loop(Map) ->
    receive
        % um tipo de msg (se recebermos este tipo de msg, executa o codigo abaixo)
        {create_account, User, Pass, From} ->
            case maps:find(User, Map) of
                error ->
                    %caso nao existir o user
                    From ! {?MODULE, created},
                    loop(maps:put(User, {Pass, true}, Map));
                _ ->
                    %caso de user existir
                    From ! {?MODULE, user_exists},
                    loop(Map)
            end;
        {close_account, User, Pass, From} ->
            case maps:find(User, Map) of
                { ok, {Pass, _} } ->
                    From ! {?MODULE, closed},
                    loop(maps:remove(User, Map));
                _ ->
                    From ! {?MODULE, invalid},
                    loop(Map)
            end;
        {login, User, Pass, From} ->
            case maps:find(User, Map) of
                { ok, {Pass, _} } ->
                    From ! {?MODULE, logged},
                    loop(maps:update(User, {Pass, true}, Map));
                _ ->
                    From ! {?MODULE, invalid},
                    loop(Map)
            end;
        {logout, User, From} ->
            case maps:find(User, Map) of
                { ok, {Pass,_} } ->
                    From ! {?MODULE, exit},
                    loop(maps:update(User, {Pass, false}, Map));
                _ ->
                    loop(Map)
            end;
        {online, From} ->
            MapOnline = maps:filter( fun(_, {_, State}) -> State end, Map),
            From ! {?MODULE, maps:keys(MapOnline)},
            loop(Map)
    end.

