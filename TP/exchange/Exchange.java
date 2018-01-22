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

public class Exchange {

    private String name;
    private ZMQ.Socket pub;
    private Map<String,Company> companies;
    private Map<String,List<Buy>> buy_orders;
    private Map<String,List<Sell>> sell_orders;
    private Map<String,List<Transaction>> transactions;

    public Exchange(ZMQ.Socket pub){
        this.pub = pub;
        this.companies = new HashMap();
        this.buy_orders = new HashMap();
        this.sell_orders = new HashMap();
        this.transactions = new HashMap();
    }

    public Exchange(ZMQ.Socket pub, Map<String,Company> companies, String name){
        this.pub = pub;
        this.name = name;
        this.companies = companies;
        this.buy_orders = new HashMap();
        this.sell_orders = new HashMap();
        this.transactions = new HashMap();
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
                pub.send(new_transaction.getCompany() + ":" + "DONE\n"); // send to subscribed clients ////falta meter os dados da transacao  
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
                pub.send(new_transaction.getCompany() + ":" + "DONE\n"); // send to subscribed clients ////falta meter os dados da transacao  
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
        
        // There's more to sell
        if(buy_qt < sell_qt){
            String sell_id = String.valueOf(getSellsByCompany(company_id).size());
            sell_order = new Sell(sell_id, sell_order.getSeller(), company_id, sell_qt - min_quantity, sell_order.getPrice());
            sell_queue.add(sell_order);
        } 
        // There's more to buy    
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

        //ZMQ.Context context = ZMQ.context(1);
        //ZMQ.Socket socket = context.socket(ZMQ.REP);
        //socket.bind("tcp://*:" + 3333);
        //ligacao ao front end
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(ZMQ.SUB);
        sub.connect("tcp://localhost:" + args[0]);

        //ligacao direta com o cliente
        ZMQ.Context context2 = ZMQ.context(1);
        ZMQ.Socket pub = context2.socket(ZMQ.PUB);
        pub.connect("tcp://localhost:" + args[1]);

        Exchange exchange = populateExchange(pub,sub,1);
        
        while(true){
            byte[] b = sub.recv();
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
                pub.send(order.getCompanyId() + ":" + client.getUser() + " put a sell order of " + order.getQuantity() + " stock shares for " + order.getPrice() + "€!"); // send to subscribed clients
                exchange.receiveSell(sell);
                //socket.send("Received sell.");
            }
            else if(type.equals("2")){
                // buy
                String buy_id = String.valueOf(exchange.getBuysByCompany(order.getCompanyId()).size());
                Buy buy = new Buy(buy_id, client.getUser(), order.getCompanyId(), order.getQuantity(), order.getPrice());
                DirectorySender.sendOrderBuy(buy); // send to directory
                pub.send(order.getCompanyId() + ":" + client.getUser() + " put a buy order of " + order.getQuantity() + " stock shares for " + order.getPrice() + "€!"); // send to subscribed clients
                exchange.receiveBuy(buy);
                //socket.send("Received buy.");
            }
        }
        //socket.close();
        //context.term();
    }

    public static Exchange populateExchange(ZMQ.Socket pub, ZMQ.Socket sub, int number){

        Map<String,Company> companies = new HashMap();
        String name = "Exchange" + number;

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
        return new Exchange(pub, companies, name);
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
