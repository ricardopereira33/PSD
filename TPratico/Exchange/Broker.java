/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;
import org.zeromq.ZMQ;

/**
 *
 * @author dinispeixoto
 */
public class Broker {
    public static void main(String[] args){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(ZMQ.XSUB);
        sub.bind("tcp://*:" + args[0]);
        ZMQ.Socket pub = context.socket(ZMQ.XPUB);
        pub.bind("tcp://*:" + args[1]);
        ZMQ.proxy(sub, pub, null); 
    }
}
