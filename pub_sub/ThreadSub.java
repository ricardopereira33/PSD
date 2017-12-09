package pub_sub;
import java.lang.Thread;
import org.zeromq.ZMQ;

public class ThreadSub extends Thread{
    ZMQ.Socket socket;
    String atualRoom;
    String id;

    public ThreadSub(ZMQ.Socket socket, String id){
        this.socket = socket;
        this.atualRoom = "default";
        this.id = id;
    }

    public void run(){
        socket.subscribe(atualRoom.getBytes());
        socket.subscribe(id.getBytes());
        
        while (true) {   
            byte[] b = socket.recv();
            String msg = new String(b);  
            
            if(msg.startsWith(id)){               
                socket.unsubscribe(atualRoom.getBytes());
                this.atualRoom = msg.substring(id.length());
                socket.subscribe(atualRoom.getBytes());
            }
            else{
                String res = msg.substring(atualRoom.length());
                System.out.println(res);
            } 
        }
    }
}