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

public class UserExample {

	public static void main(String[] args) {
		try{	
			//ZMQ.Context context = ZMQ.context(1);
         	//ZMQ.Socket socket = context.socket(ZMQ.REQ);
         	//socket.connect("tcp://localhost:3333");

         	ZMQ.Context context2 = ZMQ.context(1); // acho que aqui temos de meter 2 em vez de 1 como argumento
         	ZMQ.Socket sub = context2.socket(ZMQ.SUB);
         	sub.connect("tcp://localhost:" + args[0]);

         	ZMQ.Context context = ZMQ.context(1); 
         	ZMQ.Socket pub = context.socket(ZMQ.PUB);
         	pub.connect("tcp://localhost:" + args[1]);

         	UserSubscribeThread subscriber = new UserSubscribeThread(sub); 
    		subscriber.start();

			UserRequestExample ur = new UserRequestExample(pub,sub);
			ur.exe(pub);

			//socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
