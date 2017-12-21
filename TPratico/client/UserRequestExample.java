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

public class UserRequestExample {
    private ZMQ.Socket sock;
    private Messenger msg;

    public UserRequestExample( ZMQ.Socket socket){
        this.msg = new Messenger();
        this.sock = socket;
    }

    public void exe(ZMQ.Socket socket){
        try{   
            processOrders(socket);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void processOrders(ZMQ.Socket socket) throws IOException{
        boolean invalid = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Printer p = new Printer();
        while(invalid){
            p.printMenuOrder();
            String type = null;
            String op = br.readLine();
            switch(op){
                case "0": 
                    return;
                case "1":
                    type = "1";
                    break;
                case "2":
                    type = "2";
                    break;
                default: 
                    System.out.println("Invalid option!");
            }
            sendOrder(br, type, socket);
            //p.clear();
        }
        System.out.println("User out!");
    }

    public void sendOrder(BufferedReader br, String type, ZMQ.Socket socket) throws IOException{
        System.out.print("Company: ");
        String company = br.readLine();
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(br.readLine());
        System.out.print("Price: ");
        float price = Float.parseFloat(br.readLine());

        MsgCS order = msg.newOrder(type, company, quantity, price);
        sock.send(order.toByteArray());

        byte[] b = socket.recv();
        System.out.println(new String(b));
    }
}
