client:
	tools/protoc/bin/protoc --java_out=. proto-buf/protos.proto
	javac -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar client/*.java

server:
	tools/gpb/bin/protoc-erl -I. -maps -o front-end/ proto-buf/protos.proto
	erlc -I tools/gpb/include -o front-end/ front-end/protos.erl
	erlc -I tools/erlzmq/include -o front-end/ front-end/erlzmq.erl
	erlc -I tools/erlzmq/include -o front-end/ front-end/erlzmq_nif.erl

exchange:
	tools/protoc/bin/protoc --java_out=. proto-buf/protos.proto
	javac -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. exchange/*.java

run:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. client.User

run-client-example:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. client.UserExample

run-exchange:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. exchange.Exchange

clean:
	rm client/*.class
	rm front-end/*.beam

