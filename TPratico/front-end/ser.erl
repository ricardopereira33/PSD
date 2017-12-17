-module(ser).
-export([server/0]).

server() ->
    {ok, LSock} = gen_tcp:listen(3333, [binary, {packet, 0}, 
                                        {active, false}]),
    {ok, Sock} = gen_tcp:accept(LSock),
    io:format("accept\n"),
    case gen_tcp:recv(Sock,0) of
        {ok, Data} ->
            io:format("step1\n"),
            Map = protos:decode_msg(Data,'MsgCS'),
            io:format("step2 ~p\n",[Map]),
            case maps:find(type, Map) of
                {ok, "1"} ->
                    io:format("Type1\n");
                error ->
                    io:format("Error\n")
            end;
        _ ->
            io:format("fodeu\n")
    end,
    io:format("end\n"),
    %{ok, Bin} = gen_tcp:recv(Sock, 0),
    %Map = protos:decode_msg(Bin,'Person'),
    %io:format("map\n"),
    %io:format("received ~p~n", [Map]),
    %Bytes = protos:encode_msg(Map),
    %gen_tcp:send(Sock, Bytes),
    ok = gen_tcp:close(Sock).