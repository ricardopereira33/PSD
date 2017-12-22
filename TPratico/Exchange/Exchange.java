/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

import exchange.Protos.MsgCS;
import exchange.Protos.Request_Login;
import exchange.Protos.Client;
import exchange.Protos.Reply_Login;
import exchange.Protos.OrderRequest;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.zeromq.ZMQ;

/**
 *
 * @author dinispeixoto
 */
public class Exchange {

    private Map<String,Company> companies;
    private Map<String,List<Transaction>> transactions;
    private Map<String,List<Sell>> sell_orders;
    private Map<String,List<Buy>> buy_orders;

    public Exchange(){
            this.companies = new HashMap();
            this.transactions = new HashMap();
            this.sell_orders = new HashMap();
            this.buy_orders = new HashMap();
    }

    public Exchange(Map<String,Company> companies){
            this.companies = companies;
            this.transactions = new HashMap();
            this.sell_orders = new HashMap();
            this.buy_orders = new HashMap();
    }

    public List<Transaction> getTransactionsByCompany(String company_id){
        List<Transaction> transaction_queue = transactions.get(company_id);
        if(transaction_queue == null){
            transaction_queue = new LinkedList<Transaction>();
            transactions.put(company_id, transaction_queue);
        }
        return transaction_queue;
    }

    public List<Sell> getSellsByCompany(String company_id){
        List<Sell> sell_queue = sell_orders.get(company_id);
        if(sell_queue == null){
            sell_queue = new LinkedList<Sell>();
            sell_orders.put(company_id, sell_queue);
        }
        return sell_queue;
    }

    public List<Buy> getBuysByCompany(String company_id){
        List<Buy> buy_queue = buy_orders.get(company_id);
        if(buy_queue == null){
            buy_queue = new LinkedList<Buy>();
            buy_orders.put(company_id, buy_queue);
        }
        return buy_queue;
    }

    public void receiveSell(Sell sell_order) throws Exception{

        boolean transaction = false;
        String company_id = sell_order.getCompany();
        List<Buy> buy_queue = getBuysByCompany(company_id);
        List<Sell> sell_queue = getSellsByCompany(company_id);
        List<Transaction> transaction_list = getTransactionsByCompany(company_id);

        for(Buy buy_order : buy_queue)
            if(buy_order.getPrice() >= sell_order.getPrice()){
                transaction = true;

                Transaction new_transaction = makeTransaction(buy_order, sell_order, buy_queue, sell_queue);
                DirectorySender.sendTransaction(new_transaction); // send to directory
                transaction_list.add(new_transaction);
                transactions.put(company_id, transaction_list);
                break;
            }

        // havent found any sucessful transaction    
        if(!transaction){
            sell_queue.add(sell_order);
            sell_orders.put(company_id, sell_queue);
        }
        else receiveSell(sell_order);    
    }
    
    public void receiveBuy(Buy buy_order) throws Exception{
        
        boolean transaction = false;
        String company_id = buy_order.getCompany();
        List<Buy> buy_queue = getBuysByCompany(company_id);
        List<Sell> sell_queue = getSellsByCompany(company_id);
        List<Transaction> transaction_list = getTransactionsByCompany(company_id);
        
        for(Sell sell_order: sell_queue)
            if(buy_order.getPrice() >= sell_order.getPrice()){
                transaction = true;

                Transaction new_transaction = makeTransaction(buy_order, sell_order, buy_queue, sell_queue);
                DirectorySender.sendTransaction(new_transaction); // send to directory
                transaction_list.add(new_transaction);
                transactions.put(company_id, transaction_list);
                break;
            }
        
        // havent found any successful transaction
        if(!transaction){
            buy_queue.add(buy_order);
            buy_orders.put(company_id, buy_queue);
        }
        else receiveBuy(buy_order);
    }
      
    public Transaction makeTransaction(Buy buy_order, Sell sell_order, List<Buy> buy_queue, List<Sell> sell_queue){
        
        int buy_qt = buy_order.getQuantity();
        int sell_qt = sell_order.getQuantity();
        float mean_price = (buy_order.getPrice() + sell_order.getPrice())/2;
        int min_quantity = buy_qt < sell_qt ? buy_qt : sell_qt;
        String company_id = buy_order.getCompany();
        
        sell_queue.remove(sell_order);
        buy_queue.remove(buy_order);
        
        // Theres more to sell
        if(buy_qt < sell_qt){
            String sell_id = String.valueOf(getSellsByCompany(company_id).size());
            sell_order = new Sell(sell_id, sell_order.getSeller(), company_id, sell_qt - min_quantity, sell_order.getPrice());
            sell_queue.add(sell_order);
        } 
        // Theres more to buy    
        else if(sell_qt > buy_qt){
            String buy_id = String.valueOf(getBuysByCompany(company_id).size());
            buy_order = new Buy(buy_id, buy_order.getBuyer(), company_id, buy_qt - min_quantity, buy_order.getPrice());
            buy_queue.add(buy_order);
        }        
        
        String transaction_id = String.valueOf(getTransactionsByCompany(company_id).size());

        System.out.println("Transaction_id: " + transaction_id);
        System.out.println("Price: " + mean_price);
        System.out.println("Quantity: " + min_quantity);
        System.out.println("Company: " + company_id);

        return new Transaction(transaction_id, sell_order.getId(), buy_order.getId(), mean_price, min_quantity, company_id);
    }

    public static void main(String[] args) throws Exception{

        Exchange exchange = new Exchange();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind("tcp://*:" + 3333);
        
        while(true){
            byte[] b = socket.recv();
            MsgCS msg = MsgCS.parseFrom(b);
            System.out.println("Received " + msg.toString());
           
            OrderRequest order = msg.getOrderRequest();
            String type = order.getType();
            Client client = msg.getInfo();
            
            if(type.equals("1")){
                // sell
                String sell_id = String.valueOf(exchange.getSellsByCompany(order.getCompanyId()).size());
                Sell sell = new Sell(sell_id, client.getUser(), order.getCompanyId(), order.getQuantity(), order.getPrice());
                DirectorySender.sendOrderSell(sell); // send to directory
                exchange.receiveSell(sell);
                socket.send("Received sell.");
            }
            else if(type.equals("2")){
                // buy
                String buy_id = String.valueOf(exchange.getBuysByCompany(order.getCompanyId()).size());
                Buy buy = new Buy(buy_id, client.getUser(), order.getCompanyId(), order.getQuantity(), order.getPrice());
                DirectorySender.sendOrderBuy(buy); // send to directory
                exchange.receiveBuy(buy);
                socket.send("Received buy.");
            }
        }
    
        //socket.close();
        //context.term();
    }
    
}
