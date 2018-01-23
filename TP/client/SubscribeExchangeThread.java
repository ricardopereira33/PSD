package client;
import org.zeromq.ZMQ;

public class SubscribeExchangeThread extends Thread{
	private ZMQ.Socket socket;

	public SubscribeExchangeThread(ZMQ.Socket socket){
		this.socket = socket;
	}

	public void run(){
		while (true) {
            byte[] b = socket.recv();
            String msg = new String(b);
            String s[] = msg.split(":");
            System.out.println("\n"+s[1]);
        }
	}
}