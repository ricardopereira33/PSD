-module(login).
-export([start/0, loginManager/1, create_account/2, close_account/2, login/2, logout/1, online/0]).

%===========
% Start : spawn cria um processo, que corre o loginManager neste caso.
start() ->
    register(?MODULE, spawn( fun() -> loginManager(#{"user1" => {"33", false},"user2" => {"85", false},"user3" => {"3", false}}) end)),
    register(users, spawn( fun() -> userManager(#{}) end)).

create_account(User, Pass) ->
    ?MODULE ! { create_account, User, Pass, self()},
    receiveMsg().

close_account(User, Pass) ->
    ?MODULE ! { close_account, User, Pass, self()},
    receiveMsg().

login(User, Pass) ->
    ?MODULE ! { login, User, Pass, self()},
    receiveMsg().

logout(User) ->
    ?MODULE ! { logout, User, self()},
    receiveMsg().

online() ->
    ?MODULE ! { online, self()},
    receiveMsg().

request_pid(User) ->
    users ! { request_pid, self()},
    receiveMsg().

receiveMsg() ->
    receive
        {?MODULE, Res} -> Res
    end.

%===========
% LoginManager : manage all users in system
loginManager(Map) ->
    receive
        {create_account, User, Pass, From} ->
            case maps:find(User, Map) of
                error ->
                    From ! {?MODULE, created},
                    loginManager(maps:put(User, {Pass, true}, Map));
                _ ->
                    From ! {?MODULE, user_exists},
                    loginManager(Map)
            end;
        {close_account, User, Pass, From} ->
            case maps:find(User, Map) of
                {ok, {Pass, _}} ->
                    From ! {?MODULE, closed},
                    loginManager(maps:remove(User, Map));
                _ ->
                    From ! {?MODULE, invalid},
                    loginManager(Map)
            end;
        {login, User, Pass, From} ->
            case maps:find(User, Map) of
                {ok, {Pass, false}} ->
                    From ! {?MODULE, logged},
                    io:format("account ~p logged in\n", [User]),
                    loginManager(maps:update(User, {Pass, true}, Map));
                _ -> 
                    From ! {?MODULE, invalid},
                    loginManager(Map)
            end;
        {logout, User, From} -> 
            io:format("account ~p logged out\n", [User]),
            From ! {?MODULE, ok},
            users ! {add_pid, User, From},
            {Pass, _} = maps:get(User, Map),
            loginManager(maps:update(User, {Pass,false}, Map));
        {online, From} ->
            MapOnline = maps:filter( fun(_, {_, State}) -> State end, Map),
            From ! {?MODULE, maps:keys(MapOnline)},
            users ! {rm_pid, User},
            loginManager(Map)
    end.

userManager(Map) ->
    receive
        {request_pid, User, From} ->
            case maps:find(User, Map) of
                {ok, Pid} -> 
                    From ! {?MODULE, Pid},
                    userManager(Map);
                _ -> 
                    From ! {?MODULE ! error},
                    userManager(Map)
            end;
        {add_pid, User, From} ->
            userManager(maps:put(User, From, Map));
        {rm_pid, User} ->
            userManager(maps:remove(User, Map))
    end.
