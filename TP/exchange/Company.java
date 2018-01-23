package exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company {
    private String name;
    private Map<Integer, List<Order>> orders;
    private Map<Integer, List<Transaction>> transactions;
    private int day;

    public Company(){ }

    public Company(String name){
        this.name = name;
        this.orders = new HashMap();
        this.transactions = new HashMap();
        this.day = 0;
    }

    public String getName(){
        return name;
    }

    public List<Order> getOrdersFromDayBefore(){
        List<Order> ordersFromDayBefore = new ArrayList();
        if(!orders.containsKey(day-1) || day==0) return null;
        for(Order o : orders.get(day-1)){
            if(o.getClass().equals(Sell.class)) ordersFromDayBefore.add(o);
        }
        if(ordersFromDayBefore.isEmpty()) return null;
        return ordersFromDayBefore;
    }

    public List<Order> getOrdersFromActualDay(){
        List<Order> ordersFromActualDay = new ArrayList();
        if(!orders.containsKey(day)) return null;
        for(Order o : orders.get(day)){
            if(o.getClass().equals(Sell.class)) ordersFromActualDay.add(o);
        }
        if(ordersFromActualDay.isEmpty()) return null;
        return ordersFromActualDay;
    }

    public List<Transaction> getTransactionsFromDayBefore(){
        if(!transactions.containsKey(day-1) || day==0) return null;
        List<Transaction> transactionsFromDayBefore = transactions.get(day-1);
        if(transactionsFromDayBefore.isEmpty()) return null;
        return transactionsFromDayBefore;
    }

    public List<Transaction> getTransactionsFromActualDay(){
        if(!transactions.containsKey(day)) return null;
        List<Transaction> transactionsFromActualDay = transactions.get(day);
        if(transactionsFromActualDay.isEmpty()) return null;
        return transactionsFromActualDay;
    }

    public void addOrder(Order order){
        if(orders.containsKey(day)){
            orders.get(day).add(order);
        }
        else{
            List<Order> ordersList = new ArrayList();
            ordersList.add(order);
            orders.put(day,ordersList);
        }
    }

    public void addTransaction(Transaction transaction){
        if(transactions.containsKey(day)){
            transactions.get(day).add(transaction);
        }
        else{
            List<Transaction> transactionsList = new ArrayList();
            transactionsList.add(transaction);
            transactions.put(day,transactionsList);
        }
    }
}