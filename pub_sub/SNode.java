package pub_sub;
import org.zeromq.ZMQ;

public class SNode {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        
        ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
        xsub.bind("tcp://*:"+args[0]);
        
        ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
        xpub.bind("tcp://*:"+args[1]);
        
        ZMQ.proxy(xsub, xpub, null);
    }
}