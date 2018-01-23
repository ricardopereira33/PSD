package exchange;

import org.zeromq.ZMQ;

public class ClientExample {
    
    public static void main(String[] args){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.SUB);
        socket.connect("tcp://localhost:"+args[0]);
    }
}
