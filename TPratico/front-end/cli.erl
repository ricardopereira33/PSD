-module(cli).
-export([cli/1]).

cli(Port) ->
    SomeHostInNet = "localhost",
    {ok, Sock} = gen_tcp:connect(SomeHostInNet, Port, 
                                 [binary, {packet, 0}]),
    Bin = protos:encode_msg(#{type=>"2", repL=>#{valid => true, msg => "User&Pass"}}, 'MsgCS'),
    ok = gen_tcp:send(Sock, Bin),
    ok = gen_tcp:close(Sock).