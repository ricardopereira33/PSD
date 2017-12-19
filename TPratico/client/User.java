package client;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.Order;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

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
			CodedInputStream cis = CodedInputStream.newInstance(is);
			CodedOutputStream cos = CodedOutputStream.newInstance(os);
			MsgCS msg = createMsgCS();
			
			System.out.println("Send");
			msg.writeTo(os);
			
			MsgCS msg2 = MsgCS.parseFrom(is);
			System.out.println("-> "+ msg2.getType());
			
			System.out.println("Done.");
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static byte[] toBytes(Byte[] b){
		byte[] list = new byte[b.length];
		int i =0;
		for(Byte by : b){
			list[i] = by.byteValue();
			i++;
		}
		return list;
	}

	public static MsgCS createMsgCS() {
		return
			MsgCS.newBuilder()
			.setType("1")
			.setReqL(
				Request_Login.newBuilder()
				.setMsg("Request_Login")
				)
			.build();
	}

	public static MsgCS createMsgCS2() {
		return
			MsgCS.newBuilder()
			.setType("1")
			.setInfo(
				Client.newBuilder()
				.setUser("ze")
				.setPass("33")
				)
			.build();
	}
}
