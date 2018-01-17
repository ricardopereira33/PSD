client:
	Tools/protoc/bin/protoc --java_out=. Proto-buf/protos.proto
	javac -cp Tools/jar/protobuf-java-3.4.1.jar:Tools/jar/jeromq-0.4.3.jar Client/*.java

server:
	Tools/gpb/bin/protoc-erl -I. -maps -o Front-end/ Proto-buf/protos.proto
	erlc -I Tools/gpb/include -o Front-end/ Front-end/protos.erl
	erlc -I Tools/erlzmq/include -o Front-end/ Front-end/erlzmq.erl
	erlc -I Tools/erlzmq/include -o Front-end/ Front-end/erlzmq_nif.erl

exchange:
	Tools/protoc/bin/protoc --java_out=. Proto-buf/protos.proto
	javac -cp Tools/jar/protobuf-java-3.4.1.jar:Tools/jar/jeromq-0.4.3.jar:Tools/jar/gson-2.6.2.jar:. exchange/*.java

directory:
	mvn -f Directory/pom.xml package	

run:
	java -cp Tools/jar/protobuf-java-3.4.1.jar:Tools/jar/jeromq-0.4.3.jar:. client.User

run-client-example:
	java -cp Tools/jar/protobuf-java-3.4.1.jar:Tools/jar/jeromq-0.4.3.jar:. client.UserExample

run-exchange:
	java -cp Tools/jar/protobuf-java-3.4.1.jar:Tools/jar/jeromq-0.4.3.jar:Tools/jar/gson-2.6.2.jar:. exchange.Exchange

run-directory:
	java -jar Directory/target/Directory-1.0-SNAPSHOT.jar server 

clean:
	rm Client/*.class
	rm Front-end/*.beam

