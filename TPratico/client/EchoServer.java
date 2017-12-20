package client;
import org.zeromq.ZMQ;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.Order;

public class EchoServer {
    public static void main(String[] args) throws Exception{
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        
        socket.bind("tcp://*:" + 3333);
        
        while (true) {
            byte[] b = socket.recv();
            MsgCS msg = MsgCS.parseFrom(b);
            System.out.println("Received " + msg.toString());
        }
        //socket.close();
        //context.term();
    }
}
