package client;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.OrderRequest;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

import org.zeromq.ZMQ;

public class User {

	public static void main(String[] args) {
		try{	
         	Socket s = new Socket("localhost",3333);
         	ZMQ.Context context2 = ZMQ.context(1);
         	ZMQ.Socket sub = context2.socket(ZMQ.SUB);
         	sub.connect("tcp://localhost:" + args[0]);

         	UserSubscribeThread subscriber = new UserSubscribeThread(sub);
    		subscriber.start();

            UserRequest ur = new UserRequest(s);
			ur.exe();

			socket.close();
			sub.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
