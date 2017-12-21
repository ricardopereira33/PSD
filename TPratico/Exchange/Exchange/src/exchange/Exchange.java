/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

import java.util.HashMap;
import java.util.List;
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

    public void receiveSell(Sell sell_order){
        String company_id = sell_order.getCompany();
        List<Sell> sell_queue = sell_orders.get(company_id);
        sell_queue.add(sell_order);
        sell_orders.put(company_id, sell_queue);
    }
    
    public void receiveBuy(Buy buy_order){
        
        boolean transaction = false;
        String company_id = buy_order.getCompany();
        List<Buy> buy_queue = buy_orders.get(company_id);
        List<Sell> sell_queue = sell_orders.get(company_id);
        List<Transaction> transaction_list = transactions.get(company_id);
        
        for(Sell sell_order: sell_queue)
            if(buy_order.getPrice() >= sell_order.getPrice()){
                transaction = true;
                
                Transaction new_transaction = makeTransaction(buy_order, sell_order, buy_queue, sell_queue);
                transaction_list.add(new_transaction);
                transactions.put(company_id, transaction_list);
                break;
            }
        
        // havent found any successful transaction
        if(!transaction){
            buy_queue.add(buy_order);
            buy_orders.put(company_id, buy_queue);
        }
    }
      
    public Transaction makeTransaction(Buy buy_order, Sell sell_order, List<Buy> buy_queue, List<Sell> sell_queue){
        
        int buy_qt = buy_order.getQuantity();
        int sell_qt = sell_order.getQuantity();
        float mean_price = (buy_order.getPrice() + sell_order.getPrice())/2;
        int min_quantity = buy_qt < sell_qt ? buy_qt : sell_qt;
        String company_id = buy_order.getCompany();
        String transaction_id = String.valueOf(transactions.get(company_id).size());
        
        sell_queue.remove(sell_order);
        
        // Theres more to sell
        if(buy_qt < sell_qt){
            Sell new_sell = new Sell(sell_order.getSeller(), company_id, sell_qt - min_quantity, sell_order.getPrice());
            sell_queue.add(new_sell);
        } 
        // Theres more to buy    
        else if(sell_qt > buy_qt){
            Buy new_buy = new Buy(buy_order.getBuyer(), company_id, buy_qt - min_quantity, buy_order.getPrice());
            buy_queue.add(new_buy);
        }
        
        return new Transaction(transaction_id, sell_order, buy_order, mean_price, min_quantity, company_id);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind("tcp://*:" + 3333);
        
        byte[] b
        
        
        socket.close();
        context.term();
    }
    
}
