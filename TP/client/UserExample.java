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
			ZMQ.Context exchangeContext = ZMQ.context(1); 
         	ZMQ.Socket exchange_subscribe = exchangeContext.socket(ZMQ.SUB);
         	exchange_subscribe.connect("tcp://localhost:" + args[0]);

			ZMQ.Context frontEndContext = ZMQ.context(1); // mudar
         	ZMQ.Socket frontend_request = frontEndContext.socket(ZMQ.REQ); // mudar
         	frontend_request.connect("tcp://localhost:3333"); // mudar

         	ZMQ.Context context = ZMQ.context(1); // mudar
         	ZMQ.Socket pub = context.socket(ZMQ.PUB); // mudar
         	pub.connect("tcp://localhost:" + args[1]); // mudar

         	SubscribeExchangeThread subscriberExchange = new SubscribeExchangeThread(exchange_subscribe); 
    		subscriberExchange.start();

			UserRequestExample ur = new UserRequestExample(pub,sub); // mudar
			ur.exe(pub); // mudar

			//socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
