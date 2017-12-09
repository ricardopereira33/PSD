package pub_sub;
import org.zeromq.ZMQ;

public class PNode {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        
        ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
        xsub.connect("tcp://localhost:"+args[0]);
        xsub.connect("tcp://localhost:"+args[1]);
        
        ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
        xpub.bind("tcp://*:"+args[2]);
        
        ZMQ.proxy(xsub, xpub, null);
    }
}