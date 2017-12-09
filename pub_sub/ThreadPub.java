package pub_sub;
import org.zeromq.ZMQ;
import java.lang.Thread;

public class ThreadPub extends Thread{
    ZMQ.Socket socket;
    String atualRoom;
    String id;


    public ThreadPub(ZMQ.Socket socket, String id){
        this.socket = socket;
        this.atualRoom = "default";
        this.id = id;
    }

    public void run(){
        while (true) {
            String s = System.console().readLine();
            if(s == null) break;

            if(s.startsWith("\\room")){
                System.out.println(id);
                this.atualRoom = s.substring(6);
                socket.send(id+""+this.atualRoom);
            }
            else{
                socket.send(atualRoom+""+s);
            }
        }
    }
}