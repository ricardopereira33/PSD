package client;
import org.zeromq.ZMQ;

import java.io.*;
import java.net.*;

public class ReadExchangeThread extends Thread{
	private ZMQ.Socket socket;

	public ReadExchangeThread(ZMQ.Socket socket){
		this.socket = socket;
	}

	public void run(){
		while (true) {
            System.out.println("Entrei");
            byte[] b = socket.recv();
            String msg = new String(b);
            String s[] = msg.split(":");
            System.out.println("\n" + "[" + s[0] + "] " + s[1]);
        }
	}
}