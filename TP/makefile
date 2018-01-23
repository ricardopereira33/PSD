client:
	tools/protoc/bin/./protoc --java_out=. proto-buf/protos.proto
	javac -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar client/*.java

server:
	tools/gpb/bin/protoc-erl -I. -maps -o front-end/ proto-buf/protos.proto
	erlc -I tools/gpb/include -o front-end/ front-end/protos.erl
	erlc -I tools/erlzmq/include -o front-end/ front-end/erlzmq.erl
	erlc -I tools/erlzmq/include -o front-end/ front-end/erlzmq_nif.erl
	erlc -o front-end/ front-end/server.erl
	erlc -o front-end/ front-end/consumer.erl
	erlc -o front-end/ front-end/exchangeManager.erl
	erlc -o front-end/ front-end/login.erl
	erlc -o front-end/ front-end/producer.erl 
	erlc -o front-end/ front-end/user.erl 

exchange:
	tools/protoc/bin/./protoc --java_out=. proto-buf/protos.proto
	javac -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:tools/jar/gson-2.6.2.jar:. exchange/*.java

directory:
	mvn -f directory/pom.xml package

run-broker-client-exchange:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. exchange.BrokerClientExchange 2222 1111	

run-broker-frontend-exchange:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. exchange.BrokerFrontEndExchange 3333 4444	

run-client:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:. client.User 1111 5555

run-exchange:
	java -cp tools/jar/protobuf-java-3.4.1.jar:tools/jar/jeromq-0.4.3.jar:tools/jar/gson-2.6.2.jar:. exchange.Exchange 4444 7777 2222 1

run-directory:
	java -jar directory/target/Directory-1.0-SNAPSHOT.jar server 

clean:
	rm client/*.class
	rm front-end/*.beam