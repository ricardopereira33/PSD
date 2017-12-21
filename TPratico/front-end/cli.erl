-module(cli).
-export([cli/0]).

cli() ->
    {ok, C} = erlzmq:context(),
    {ok, Socket} = erlzmq:socket(C, [req, {active, false}]),

    ok = erlzmq:connect(Socket, "tcp://localhost:3333"),
    Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "User&Pass"}}, 'MsgCS'),
    io:format("Sending\n"),
    erlzmq:send(Socket, Bin),
    {ok, R} = erlzmq:recv(Socket),
    Msg = protos:decode_msg(R, 'MsgCS'),
    io:format("Received '~p'~n", [Msg]).