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

public class UserRequest {
    private Socket sock;
    private ZMQ.Socket sub;
    private InputStream ins;
    private OutputStream outs;
    private Messenger msg;
    

    public UserRequest( Socket socket, ZMQ.Socket sub){
        this.msg = new Messenger();
        this.sock = socket;
        this.sub = sub;
    }

    public void exe(){
        try{    
            this.ins = sock.getInputStream();
            this.outs = sock.getOutputStream();
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
            writeMsg(request);
            MsgCS reply = MsgCS.parseFrom(readMsg());

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
            writeMsg(client);
            MsgCS reply = MsgCS.parseFrom(readMsg());

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
                    sendOrder(br, "1");
                    break;
                case "2":
                    sendOrder(br, "2");
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

        MsgCS order = msg.newOrderRequest("POR FAZER",type, company, quantity, price);
        writeMsg(order);
        MsgCS reply = MsgCS.parseFrom(readMsg());

        System.out.println("ACK: " + reply.getType());
    }

    public byte[] readMsg(){
        byte[] res = null;
        try{
            byte[] b = new byte[1024];
            int n = ins.read(b);
            
            res = new byte[n];
            for(int i = 0; i < n; i++){
                res[i] = b[i];
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }

    public void writeMsg(MsgCS msg){
        try{
            outs.write(msg.toByteArray());
            outs.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
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
