package tutorial;

import tutorial.Protos.AddressBook;
import tutorial.Protos.Person;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) {
		try{	
			Socket s = new Socket("localhost", 3333);
			CodedInputStream cis = CodedInputStream.newInstance(s.getInputStream());
			CodedOutputStream cos = CodedOutputStream.newInstance(s.getOutputStream());
			Person p = createPerson();
			byte[] b = p.toByteArray();
			int len = b.length;

			System.out.println("Send");
			cos.writeRawBytes(b);
			cos.flush();
			
			byte[] ba = cis.readRawBytes(len);
			Person p2 = Person.parseFrom(ba);
			System.out.println("-> "+ p2.getName());
			System.out.println("Done.");
			
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static Person createPerson() {
		return
			Person.newBuilder()
			.setId(1234)
			.setName("John Doe")
			.setEmail("jdoe@example.com")
			.build();
	}
}