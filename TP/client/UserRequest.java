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
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import org.zeromq.ZMQ;

public class UserRequest {

    private Socket frontend;
    private ZMQ.Socket exchange_subscribe;
    private InputStream ins;
    private OutputStream outs;
    private String user;
    
    public UserRequest( Socket frontend, ZMQ.Socket exchange_subscribe){
        this.frontend = frontend;
        this.exchange_subscribe = exchange_subscribe;
    }

    public void exe(){
        try{    
            this.ins = frontend.getInputStream();
            this.outs = frontend.getOutputStream();
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
            MsgCS request = Messenger.newReqLogin();
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

            MsgCS client = Messenger.newClient(user, pass);
            writeMsg(client);
            MsgCS reply = MsgCS.parseFrom(readMsg());

            if(reply.getRepL().getValid()){
                this.user = user;
                invalid = false;
            }
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
                    System.exit(0); // TEMOS DE FAZER PEDIDO PARA LOGOUT!!!
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
                case "5":
                    try{
                        getCompanies(br);
                    }
                    catch(Exception e){ e.printStackTrace();}
                    br.readLine();
                    break;
                case "6":
                    try{
                        getCompanyInfo(br);
                    }
                    catch(Exception e){ e.printStackTrace();}
                    br.readLine();
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

        MsgCS order = Messenger.newOrderRequest(this.user, type, company, quantity, price);
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
        exchange_subscribe.subscribe(company.getBytes());
        System.out.println("Company Subscribed");
    }

    public void unsubscribeCompany(BufferedReader br) throws IOException{
        //testar
        System.out.print("Company: ");
        String company = br.readLine();
        exchange_subscribe.unsubscribe(company.getBytes());
        System.out.println("Company Unsubscribed");
    }

    public void getCompanies(BufferedReader br) throws Exception{
        String urly = "http://localhost:8080/companies";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        JSONArray jsonArray = new JSONArray(result.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String company = object.getString("name");
            System.out.println("-" + company);
        }
    }

    public void getCompanyInfo(BufferedReader br) throws Exception{
        System.out.print("Company: ");
        String company = br.readLine();
        String urly = "http://localhost:8080/company/" + company + "/info";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        JSONObject companyInfo = new JSONObject(result.toString());
        String name = companyInfo.getString("company");
        System.out.println("\nCompany Name: " + name);
        String exchange = companyInfo.getString("exchange");
        System.out.println("Exchange: " + exchange + " (" + companyInfo.getString("host") + ":" + companyInfo.getString("port") + ")");
        System.out.println("Today Transactions:");
        double todayStartPrice = companyInfo.getDouble("todayStartPrice");
        System.out.println("\t-Start Price: " + todayStartPrice);
        double todayEndPrice = companyInfo.getDouble("todayEndPrice");
        System.out.println("\t-End Price: " + todayEndPrice);
        double todayMinPrice = companyInfo.getDouble("todayMinPrice");
        System.out.println("\t-Minimum Price: " + todayMinPrice);
        double todayMaxPrice = companyInfo.getDouble("todayMaxPrice");
        System.out.println("\t-Maximum Price: " + todayMaxPrice);
        System.out.println("Yesterday Transactions:");
        double yesterdayStartPrice = companyInfo.getDouble("yesterdayStartPrice");
        System.out.println("\t-Start Price: " + yesterdayStartPrice);
        double yesterdayEndPrice = companyInfo.getDouble("yesterdayEndPrice");
        System.out.println("\t-End Price: " + yesterdayStartPrice);
        double yesterdayMinPrice = companyInfo.getDouble("yesterdayMinPrice");
        System.out.println("\t-Minimum Price: " + yesterdayStartPrice);
        double yesterdayMaxPrice = companyInfo.getDouble("yesterdayMaxPrice");
        System.out.println("\t-Maximum Price: " + yesterdayStartPrice);
    }
}
