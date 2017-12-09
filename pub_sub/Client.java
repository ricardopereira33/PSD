package pub_sub;
import org.zeromq.ZMQ;
import java.lang.Thread;

public class Client {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socketPub = context.socket(ZMQ.PUB);
        ZMQ.Socket socketSub = context.socket(ZMQ.SUB);
        
        socketPub.connect("tcp://localhost:"+args[0]);
        socketSub.connect("tcp://localhost:"+args[1]);
        
        ThreadPub tp = new ThreadPub(socketPub, args[2]);
        ThreadSub ts = new ThreadSub(socketSub, args[2]);
        
        tp.start();
        ts.start();

        try{
            tp.join();
            ts.join();
        }
        catch(Exception e){
            System.out.println("Erro: " + e.getMessage());
        }
             
        socketSub.close();
        socketPub.close();
        context.term();
    }
}
