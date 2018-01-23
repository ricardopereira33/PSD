package exchange; // acho que devia estar noutro package
import org.zeromq.ZMQ;

public class BrokerFrontEndExchange{ 

    public static void main(String[] args) {

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket push = context.socket(ZMQ.PUSH);
        push.bind("tcp://*:" + args[0]);
        ZMQ.Socket pull = context.socket(ZMQ.PULL);
        pull.bind("tcp://*:" + args[1]);
        ZMQ.proxy(pull, push, null);

    }
}