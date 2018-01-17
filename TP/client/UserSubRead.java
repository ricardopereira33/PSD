package client;
import org.zeromq.ZMQ;

public class UserSubRead extends Thread{
	ZMQ.Socket sub;

	public UserSubRead(ZMQ.Socket sub){
		this.sub = sub;
	}

	public void run(){
		while (true) {
      		byte[] b = sub.recv();
      		String s = new String(b);
      		String ss[] = s.split(":");
      		System.out.println("\n"+ss[1]); // Imprime mensagem que recebe da exange
            }
	}
}