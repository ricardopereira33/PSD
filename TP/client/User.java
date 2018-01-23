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
			ZMQ.Context exchangeContext = ZMQ.context(1); 
         	ZMQ.Socket exchange_subscribe = exchangeContext.socket(ZMQ.SUB);
         	exchange_subscribe.connect("tcp://localhost:" + args[0]);

			Socket frontend = new Socket("localhost",Integer.parseInt(args[1]));
 
         	new ReadExchangeThread(exchange_subscribe).start(); 
         	new UserRequest(frontend, exchange_subscribe).exe();

			frontend.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
