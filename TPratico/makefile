client:
	tools/protoc/bin/protoc --java_out=. proto-buf/protos.proto
	javac -cp tools/protobuf-java-3.4.1.jar client/*.java

server:
	tools/gpb/bin/protoc-erl -I. -maps -o front-end/ proto-buf/protos.proto
	erlc -I tools/gpb/include -o front-end/ front-end/protos.erl

run:
	java -cp tools/protobuf-java-3.4.1.jar:. client.User

