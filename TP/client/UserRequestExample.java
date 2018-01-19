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

public class UserRequestExample {
    private ZMQ.Socket sock;
    private ZMQ.Socket sub;
    private Messenger msg;

    public UserRequestExample( ZMQ.Socket socket, ZMQ.Socket sub){
        this.msg = new Messenger();
        this.sock = socket;
        this.sub = sub;
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
                    sendOrder(br, "1", socket);
                    break;
                case "2":
                    sendOrder(br, "2", socket);
                    break;
                case "3":
                    subscribeCompany(br);
                    break;
                case "4":
                    unsubscribeCompany(br);
                    break;
                default: 
                    System.out.println("Invalid option!");
            }
            //sendOrder(br, type, socket);
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

        MsgCS order = msg.newOrderRequest("POR FAZER",type, company, quantity, price);
        sock.send(order.toByteArray());

        //byte[] b = socket.recv();
        //System.out.println(new String(b));
        System.out.println("Order done");
    }

    public void subscribeCompany(BufferedReader br) throws IOException{
        //testar
        System.out.print("Company: ");
        String company = br.readLine();
        sub.subscribe(company.getBytes());
        System.out.println("Company Subscribed");

    }

    public void unsubscribeCompany(BufferedReader br) throws IOException{
        //testar
        System.out.print("Company: ");
        String company = br.readLine();
        sub.unsubscribe(company.getBytes());
        System.out.println("Company Unsubscribed");
    }
}
