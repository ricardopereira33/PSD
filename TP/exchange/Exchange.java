package exchange;

import exchange.Protos.MsgCS;
import exchange.Protos.Request_Login;
import exchange.Protos.Client;
import exchange.Protos.Reply_Login;
import exchange.Protos.OrderRequest;
import exchange.Protos.OrderReply;
import client.Messenger;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.zeromq.ZMQ;

public class Exchange {

    private String name;
    private ZMQ.Socket client_pub;
    private ZMQ.Socket frontend_push;
    private ZMQ.Socket frontend_pull;
    private Map<String,Company> companies;
    private Map<String,List<Buy>> buy_orders;
    private Map<String,List<Sell>> sell_orders;
    private Map<String,List<Transaction>> transactions;

    public Exchange(String name, ZMQ.Socket frontend_push, ZMQ.Socket frontend_pull, ZMQ.Socket client_pub, Map<String,Company> companies){
        this.name = name;
        this.frontend_push = frontend_push;
        this.frontend_pull = frontend_pull;
        this.client_pub = client_pub;
        this.companies = companies;
        this.buy_orders = new HashMap();
        this.sell_orders = new HashMap();
        this.transactions = new HashMap();
    }

    public void receiveSell(Sell sell_order) throws Exception{

        boolean transaction = false;
        String company = sell_order.getCompany();
        List<Buy> buy_queue = getBuysByCompany(company);
        List<Sell> sell_queue = getSellsByCompany(company);
        List<Transaction> transaction_list = getTransactionsByCompany(company);

        for(Buy buy_order : buy_queue)
            if(buy_order.getPrice() >= sell_order.getPrice()){
                transaction = true;

                Transaction new_transaction = makeTransaction(buy_order, sell_order, buy_queue, sell_queue);
                DirectorySender.sendTransaction(new_transaction); // send to directory
                client_pub.send("[ " + new_transaction.getCompany() + " ] : " + new_transaction.getQuantity() + " units - " + new_transaction.getPrice() + "€ -- Transaction\n"); // send to subscribed clients ////falta meter os dados da transacao  
                frontend_push.send(Messenger.newOrderReply(sell_order.getSeller(),"[ " + new_transaction.getCompany() + " ] : " + new_transaction.getQuantity() + " units - " + new_transaction.getPrice() + "€ -- Transaction\n").toByteArray());
                transaction_list.add(new_transaction);
                transactions.put(company, transaction_list);
                break;
            }

        // havent found any sucessful transaction    
        if(!transaction){
            sell_queue.add(sell_order);
            sell_orders.put(company, sell_queue);
        }
    }
    
    public void receiveBuy(Buy buy_order) throws Exception{
        
        boolean transaction = false;
        String company = buy_order.getCompany();
        List<Buy> buy_queue = getBuysByCompany(company);
        List<Sell> sell_queue = getSellsByCompany(company);
        List<Transaction> transaction_list = getTransactionsByCompany(company);

        for(Sell sell_order: sell_queue)
            if(buy_order.getPrice() >= sell_order.getPrice()){
                transaction = true;

                Transaction new_transaction = makeTransaction(buy_order, sell_order, buy_queue, sell_queue);
                DirectorySender.sendTransaction(new_transaction); // send to directory
                client_pub.send("[ " + new_transaction.getCompany() + " ] : " + new_transaction.getQuantity() + " units - " + new_transaction.getPrice() + "€ -- Transaction\n"); // send to subscribed clients ////falta meter os dados da transacao  
                frontend_push.send(Messenger.newOrderReply(buy_order.getBuyer(),"[ " + new_transaction.getCompany() + " ] : " + new_transaction.getQuantity() + " units - " + new_transaction.getPrice() + "€ -- Transaction\n").toByteArray());
                transaction_list.add(new_transaction);
                transactions.put(company, transaction_list);
                break;
            }
        
        // havent found any successful transaction
        if(!transaction){
            buy_queue.add(buy_order);
            buy_orders.put(company, buy_queue);
        }
    }
      
    public Transaction makeTransaction(Buy buy_order, Sell sell_order, List<Buy> buy_queue, List<Sell> sell_queue) throws Exception{
        
        int buy_qt = buy_order.getQuantity();
        int sell_qt = sell_order.getQuantity();
        float mean_price = (buy_order.getPrice() + sell_order.getPrice())/2;
        int min_quantity = buy_qt < sell_qt ? buy_qt : sell_qt;
        String company = buy_order.getCompany();
        
        sell_queue.remove(sell_order);
        buy_queue.remove(buy_order);
        
        // There's more to sell
        if(buy_qt < sell_qt){
            String sell_id = String.valueOf(getSellsByCompany(company).size());
            sell_order = new Sell(sell_id, sell_order.getSeller(), company, sell_qt - min_quantity, sell_order.getPrice());
            receiveSell(sell_order);
        } 
        // There's more to buy    
        else if(sell_qt < buy_qt){
            String buy_id = String.valueOf(getBuysByCompany(company).size());
            buy_order = new Buy(buy_id, buy_order.getBuyer(), company, buy_qt - min_quantity, buy_order.getPrice());
            receiveBuy(buy_order);
        }        
        
        String transaction_id = String.valueOf(getTransactionsByCompany(company).size());

        System.out.println("Transaction_id: " + transaction_id);
        System.out.println("Price: " + mean_price);
        System.out.println("Quantity: " + min_quantity);
        System.out.println("Company: " + company);

        return new Transaction(transaction_id, sell_order.getId(), buy_order.getId(), mean_price, min_quantity, company);
    }

    public static void main(String[] args) throws Exception{

        // ARGS
        // 0 -> FrontEnd PUSH
        // 1 -> FrontEnd PULL
        // 2 -> Client PUB
        // 3 -> Exchange type (1, 2 or 3)

        // Front-end connections
        ZMQ.Context frontEndContext = ZMQ.context(1);
        ZMQ.Socket frontend_push = frontEndContext.socket(ZMQ.PUSH);
        frontend_push.connect("tcp://localhost:" + args[0]);
        ZMQ.Socket frontend_pull = frontEndContext.socket(ZMQ.PULL);
        frontend_pull.bind("tcp://localhost:" + args[1]);

        // Client direct connection
        ZMQ.Context clientContext = ZMQ.context(1);
        ZMQ.Socket client_pub = clientContext.socket(ZMQ.PUB);
        client_pub.connect("tcp://localhost:" + args[2]);

        Exchange exchange = new Exchange("Exchange" + args[3], frontend_push, frontend_pull, client_pub, populateCompanies(Integer.parseInt(args[3])));
        
        while(true){
            byte[] b = frontend_pull.recv();
            MsgCS msg = MsgCS.parseFrom(b);
            System.out.println("Received " + msg.toString()); // provavelmente é para tirar isto!!!
           
            OrderRequest order = msg.getOrderRequest();
            String type = order.getType();
            String company = msg.getCompany();
            Client client = msg.getInfo();
            
            if(type.equals("1")){
                // SELL
                String sell_id = String.valueOf(exchange.getSellsByCompany(order.getCompanyId()).size());
                Sell sell = new Sell(sell_id, client.getUser(), company, order.getQuantity(), order.getPrice());
                DirectorySender.sendOrderSell(sell); // send to directory
                client_pub.send(company + ":" + client.getUser() + " put a sell order of " + order.getQuantity() + " stock shares for " + order.getPrice() + " €!"); // send to subscribed clients
                frontend_push.send(Messenger.newOrderReply(sell.getSeller(),"Order to " + sell.getCompany()).toByteArray());
                exchange.receiveSell(sell);
            }
            else if(type.equals("2")){
                // BUY
                String buy_id = String.valueOf(exchange.getBuysByCompany(order.getCompanyId()).size());
                Buy buy = new Buy(buy_id, client.getUser(), company, order.getQuantity(), order.getPrice());
                DirectorySender.sendOrderBuy(buy); // send to directory
                client_pub.send(company + ":" + client.getUser() + " put a buy order of " + order.getQuantity() + " stock shares for " + order.getPrice() + " €!"); // send to subscribed clients
                frontend_push.send(Messenger.newOrderReply(buy.getBuyer(),"Order to " + buy.getCompany()).toByteArray());
                exchange.receiveBuy(buy);
            }
        }
    }

    public void printExchangeState(){
        
        List<Buy> buy_queue = getBuysByCompany("Apple");
        List<Sell> sell_queue = getSellsByCompany("Apple");
        List<Transaction> transaction_queue = getTransactionsByCompany("Apple");

        System.out.println("BUY ORDERS");
        for(Buy buy: buy_queue)
            System.out.println("Id:" + buy.getId() + ", Quantity:" + buy.getQuantity() + ", Price: " + buy.getPrice());
        
        System.out.println("SELL ORDERS");
        for(Sell sell: sell_queue)
            System.out.println("Id:" + sell.getId() + ", Quantity:" + sell.getQuantity() + ", Price: " + sell.getPrice());

        System.out.println("TRANSACTIONS");
        for(Transaction transaction: transaction_queue)
            System.out.println("Id:" + transaction.getId() + ", Sell: " + transaction.getSellOrder() + ", Buy: " + transaction.getBuyOrder() + ", Quantity:" + transaction.getQuantity() + ", Price: " + transaction.getPrice());
    }

    public static Map<String,Company> populateCompanies(int number){

        Map<String,Company> companies = new HashMap();

        switch(number){
            case 1: 
                companies.put("Apple",new Company("Apple"));
                companies.put("Samsung",new Company("Samsung"));
                companies.put("Xiaomi",new Company("Xiaomi"));
            case 2:
                companies.put("Google",new Company("Google"));
                companies.put("Facebook",new Company("Facebook"));
                companies.put("Twitter",new Company("Twitter"));
            case 3:
                companies.put("Amazon",new Company("Amazon"));
                companies.put("Ebay",new Company("Ebay"));
                companies.put("AliExpress",new Company("AliExpress"));
            default: break;
        }
        return companies;
    }

    public List<Transaction> getTransactionsByCompany(String company){
        List<Transaction> transaction_queue = transactions.get(company);
        if(transaction_queue == null){
            transaction_queue = new LinkedList<Transaction>();
            transactions.put(company, transaction_queue);
        }
        return transaction_queue;
    }

    public List<Sell> getSellsByCompany(String company){
        List<Sell> sell_queue = sell_orders.get(company);
        if(sell_queue == null){
            sell_queue = new LinkedList<Sell>();
            sell_orders.put(company, sell_queue);
        }
        return sell_queue;
    }

    public List<Buy> getBuysByCompany(String company){
        List<Buy> buy_queue = buy_orders.get(company);
        if(buy_queue == null){
            buy_queue = new LinkedList<Buy>();
            buy_orders.put(company, buy_queue);
        }
        return buy_queue;
    }
}
