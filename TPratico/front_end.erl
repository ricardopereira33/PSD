-module(front_end).
-export([server/1]).

server(Port) ->
    LoginManager = spawn(fun()-> loginManager(#{}) end),
    process_flag(trap_exit, true),
    {ok, LSock} = gen_tcp:listen(Port, [binary, {packet, 0}, {active, false}]),
    acceptor(LSock, LoginManager).

acceptor(LSock, LoginManager) ->
    {ok, Sock} = gen_tcp:accept(LSock),
    io:format("Accept connection\n"),
    spawn(fun() -> acceptor(LSock, LoginManager) end),
    waitLogin(LoginManager, Sock).

waitLogin(LoginManager, Sock) ->
    receive
        {tcp, _, Data} ->
            case string:tokens(Op, "\n\t\r ") of
                ["1"] ->
                    gen_tcp:send(Sock, "Username: "),
                    receive
                        {tcp, _, Username} ->
                            [U] = string:tokens(Username, "\n\t\r ")
                    end,
                    gen_tcp:send(Sock, "Password: "),
                    receive
                        {tcp, _, Password} ->
                            [P] = string:tokens(Password, "\n\t\r ")
                    end,
                    LoginManager ! {login, U, P, self()},
                    receive
                        {LoginManager, logged} ->
                            gen_tcp:send(Sock, "\nLogged in as "++U++"!\n"),
                            gen_tcp:send(Sock, "\nOrdens:\n \\venda 'Empresa' 'Quantidade' 'Preco Minimo'\n \\compra 'Empresa' 'Quantidade' 'Preco Maximo'\n"),
                            user(Sock, U, LoginManager);
                        {LoginManager, invalid} ->
                            gen_tcp:send(Sock, "\nUsername or password incorrect!\n\n"),
                            waitLogin(LoginManager, Sock)
                    end;
                ["2"] -> 
                    gen_tcp:send(Sock, "Username: "),
                    receive
                        {tcp, _, Username} ->
                            [U] = string:tokens(Username, "\n\t\r ")
                    end,
                    gen_tcp:send(Sock, "Password: "),
                    receive
                        {tcp, _, Password} ->
                            [P] = string:tokens(Password, "\n\t\r ")
                    end,
                    LoginManager ! {create_account, U, P, self()},
                    receive
                        {LoginManager, created} ->
                            gen_tcp:send(Sock, "\nAccount created!\n\n"),
                            waitLogin(LoginManager, Sock);
                        {LoginManager, user_exists} ->
                            gen_tcp:send(Sock, "\nUsername already used!\n\n"),
                            waitLogin(LoginManager, Sock)
                    end;
                _ ->
                    gen_tcp:send(Sock, "\nInvalid Option!\n\n"),
                    waitLogin(LoginManager, Sock)
            end
    end.

loginManager(M) ->
    receive
        {create_account, U, P, From} ->
            case maps:find(U, M) of
                error ->
                    From ! {self(), created},
                    io:format("account ~p created~n", [U]),
                    loginManager(maps:put(U, {P, false}, M));
                _ -> 
                    From ! {self(), user_exists},
                    loginManager(M)
            end;
        {{close_account, U, P}, From} ->
            case maps:find(U, M) of
                {ok,{P, _}} ->
                    From ! {self(), ok},
                    io:format("account ~p deleted~n", [U]),
                    loginManager(maps:remove(U, M));
                _ -> 
                    From ! {self(), invalid},
                    loginManager(M)
            end;
        {login, U, P, From} ->
            case maps:find(U, M) of
                {ok,{P, false}} ->
                    From ! {self(), logged},
                    io:format("account ~p logged in~n", [U]),
                    loginManager(maps:update(U,{P,true}, M));
                _ -> 
                    From ! {self(), invalid},
                    loginManager(M)
            end;
        {logout, U, From} -> 
            From ! {self(), ok},
            io:format("account ~p logged out~n", [U]),
            {P,_} = maps:get(U, M),
            loginManager(maps:update(U, {P,false}, M));
        {online, From} ->
            Res = [U || {U, {_P, true}} <- maps:to_list(M)],
            From ! {self(), Res},
            loginManager(M)
    end.


user(Sock, Username, LoginManager) ->
    receive
        {tcp, _, Data} ->
            case string:tokens(Data, "\n\r\t ") of
                ["\\venda", Empr, Quant, PMin] ->
            
                    user(Sock, Username, LoginManager);
                ["\\compra", Empr, Quant, PMax] ->

                    user(Sock, Username, LoginManager);
                ["\\logout"] ->
                    LoginManager ! {logout, Username, self()},
                    receive
                        {_, ok} -> waitLogin(LoginManager, Sock)
                    end;
                _ ->
                    gen_tcp:send(Sock,"\nOrdem inválida!\n\n"),
                    user(Sock, Username, LoginManager)
            end;
        {tcp_closed, _} ->
            LoginManager ! {logout, Username, self()},
            receive
                {_, ok} -> true
            end;
        {tcp_error, _, _} ->
            true    
    end.
