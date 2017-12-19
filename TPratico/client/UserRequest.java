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

public class UserRequest {
    private InputStream is;
    private OutputStream os;
    private Messenger msg;

    public UserRequest(InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
        this.msg = new Messenger();
    }

    public void exe(){
        try{    
            connectToServer();
            login();
            processOrders();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void connectToServer() throws IOException{
        boolean invalid = true;

        while(invalid){
            MsgCS request = msg.newReqLogin();
            request.writeTo(os);

            byte[] by = new byte[256];
            int n = is.read(by);
            byte[] by2 = getByteArrayClean(by, n);

            MsgCS reply = MsgCS.parseFrom(by2);

            if(reply.getRepL().getValid())
                invalid = false;
        }
        System.out.println("Connected");
    }

    public void login() throws IOException{
        boolean invalid = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(invalid){
            System.out.print("Username: ");
            String user = br.readLine();
            System.out.print("Password: ");
            String pass = br.readLine();

            MsgCS client = msg.newClient(user, pass);
            client.writeTo(os);

            byte[] by = new byte[256];
            int n = is.read(by);
            byte[] by2 = getByteArrayClean(by, n);

            MsgCS reply = MsgCS.parseFrom(by2);

            if(reply.getRepL().getValid())
                invalid = false;
            else System.out.println("Invalid!");
        }
        System.out.println("User logged!");
    }

    public void processOrders() throws IOException{
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
            sendOrder(br, type);
            p.clear();
        }
        System.out.println("User out!");
    }

    public void sendOrder(BufferedReader br, String type) throws IOException{
        System.out.print("Company: ");
        String company = br.readLine();
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(br.readLine());
        System.out.print("Price: ");
        float price = Float.parseFloat(br.readLine());

        MsgCS order = msg.newOrder(type, company, quantity, price);
        order.writeTo(os);
    }

    public byte[] getByteArrayClean(byte[] b, int n){
        byte[] list = new byte[n];
        for(int i = 0; i<n; i++){
            list[i] = b[i];
        }
        return list;
    }   
}
