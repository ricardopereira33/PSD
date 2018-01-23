package client;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.OrderRequest;
import client.Protos.OrderReply;

import java.io.*;
import java.net.*;

public class ReadFrontEndThread extends Thread{
	
	private Socket frontend;
	private InputStream inputStream;

	public ReadFrontEndThread(Socket frontend){
		this.frontend = frontend;
	}

	public void run(){
		try{
			this.inputStream = frontend.getInputStream();
			while(true){			
				byte[] b = readMsg();
				MsgCS msg = MsgCS.parseFrom(b);
				OrderReply reply = msg.getOrderReply();
				String notification = reply.getNotification();
				System.out.println("\n" + notification);
			}
		}
		catch(Exception e){ e.printStackTrace();}
	}

	public byte[] readMsg(){
        byte[] res = null;
        try{
            byte[] b = new byte[2048];
            int n = inputStream.read(b);
            
            res = new byte[n];
            for(int i = 0; i < n; i++){
                res[i] = b[i];
            }
        }
        catch(Exception e){ e.printStackTrace();}
        return res;
    }
}