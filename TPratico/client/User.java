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

public class User {

	public static void main(String[] args) {
		try{	
			Socket s = new Socket("localhost", 3333);
			CodedInputStream cis = CodedInputStream.newInstance(s.getInputStream());
			CodedOutputStream cos = CodedOutputStream.newInstance(s.getOutputStream());
			MsgCS msg = createMsgCS();
			byte[] b = msg.toByteArray();
			int len = b.length;

			System.out.println("Send");
			cos.writeRawBytes(b);
			cos.flush();
			
			List<Byte> list = new ArrayList<>();
			while(!cis.isAtEnd()){
				list.add(cis.readRawByte());	
			}
			byte[] ba = list. 
			MsgCS msg2 = MsgCS.parseFrom(ba);
			System.out.println("-> "+ msg2.getType());

			msg = createMsgCS2();
			b = msg.toByteArray();
			len = b.length;
			System.out.println("Send");
			cos.writeRawBytes(b);
			cos.flush();

			ba = cis.readRawBytes(len);
			msg2 = MsgCS.parseFrom(ba);
			System.out.println("-> "+ msg2.getType());

			System.out.println("Done.");
			
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
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
