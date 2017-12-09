-module(server).
-export([server/0]).

server() ->
    {ok, LSock} = gen_tcp:listen(3333, [binary, {packet, 0}, 
                                        {active, false}]),
    {ok, Sock} = gen_tcp:accept(LSock),
    io:format("accept\n"),
    {ok, Bin} = gen_tcp:recv(Sock, 0),
    io:format("finish\n"),
    Map = protos:decode_msg(Bin,'Person'),
    io:format("map\n"),
    io:format("received ~p~n", [Map]),
    Bytes = protos:encode_msg(Map),
    gen_tcp:send(Sock, Bytes),
    ok = gen_tcp:close(Sock).