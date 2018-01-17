import org.zeromq.ZMQ;

public class BrokerCliEx{
	public static void main(String[] args) {
	   ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(ZMQ.XSUB);
        sub.bind("tcp://*:"+args[0]);
        ZMQ.Socket pub = context.socket(ZMQ.XPUB);
        pub.bind("tcp://*:"+args[1]);
        ZMQ.proxy(sub, pub, null);

        sub.close();
        pub.close();
        context.term();
	}
}