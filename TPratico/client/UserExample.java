package client;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.Order;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

import org.zeromq.ZMQ;

public class UserExample {

	public static void main(String[] args) {
		try{	
			ZMQ.Context context = ZMQ.context(1);
         	ZMQ.Socket socket = context.socket(ZMQ.REQ);
         	socket.connect("tcp://localhost:3333");

			UserRequestExample ur = new UserRequestExample(socket);
			ur.exe(socket);

			socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
