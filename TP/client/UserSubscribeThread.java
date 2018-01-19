package client;
import org.zeromq.ZMQ;

public class UserSubscribeThread extends Thread{
	ZMQ.Socket socket;

	public UserSubscribeThread(ZMQ.Socket socket){
		this.socket = socket;
	}

	public void run(){
        //socket.subscribe("".getBytes());
		while (true) {
            byte[] b = socket.recv();
            String msg = new String(b);
            String s[] = msg.split(":");
            System.out.println("\n"+s[1]);
        }
	}
}