package tutorial;

import tutorial.Protos.MsgCS;
import tutorial.Protos.Request_Login;
import tutorial.Protos.Client;
import tutorial.Protos.Reply_Login;
import tutorial.Protos.Order;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.net.*;

public class Cli {

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
			
			//byte[] ba = cis.readRawBytes(len);
			//Person p2 = Person.parseFrom(ba);
			//System.out.println("-> "+ p2.getName());
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
			/*.setInfo(
				Client.newBuilder()
				.setUser("ze")
				.setPass("33")
				)*/
			/*.setRepL(
				Reply_Login.newBuilder()
				.setMsg("Reply_Login")
				.setValid(true)
				)*/
			.setReqL(
				Request_Login.newBuilder()
				.setMsg("Request_Login")
				)
			/*.setOrder(
				Order.newBuilder()
				.setType("1")
				.setCompanyId("com")
				.setQuantity(33)
				.setPrice(3)
				)*/
			.build();
	}
}
