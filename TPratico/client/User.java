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

public class User {

	public static void main(String[] args) {
		try{	
			Socket s = new Socket("localhost", 3333);
			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();

			UserRequest ur = new UserRequest(is, os);
			ur.exe();

			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
