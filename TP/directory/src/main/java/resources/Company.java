package resources;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
    private String name;
    private String host;
    private String port;
    private String description;
    private String exchange;
    private Map<Integer, List<Order>> orders;
    private Map<Integer, List<Transaction>> transactions;
    private Clock clock;

    public Company(){}

    public Company(String name, String host, String port, String description, String exchange, Clock clock){
        this.name = name;
        this.host = host;
        this.port = port;
        this.description = description;
        this.exchange = exchange;
        this.orders = new HashMap();
        this.transactions = new HashMap();
        this.clock = clock;
    }

    public String getName(){
        return name;
    }

    public String getHost(){
        return host;
    }

    public String getPort() {return port; }

    public String getDescription() {
        return description;
    }

    public String getExchange(){
        return exchange;
    }

    public void addOrder(Order order){
        //LocalDate today = LocalDate.now();
        int today = clock.getDay();
        if(orders.containsKey(today)){
            orders.get(today).add(order);
        }
        else{
            List<Order> ordersList = new ArrayList();
            ordersList.add(order);
            orders.put(today,ordersList);
        }
    }

    public void addTransaction(Transaction transaction){
        //LocalDate today = LocalDate.now();
        int today = clock.getDay();
        if(transactions.containsKey(today)){
            transactions.get(today).add(transaction);
        }
        else{
            List<Transaction> transactionsList = new ArrayList();
            transactionsList.add(transaction);
            transactions.put(today,transactionsList);
        }
    }

    public PriceInfo getTodayPrices(){
        //LocalDate today = LocalDate.now();
        int today = clock.getDay();
        PriceInfo todayPrices = new PriceInfo();
        if(transactions.containsKey(today)){
            List<Transaction> todayTransList = transactions.get(today);
            float startPrice, endPrice, maxPrice = 0, minPrice = 0;
            startPrice = todayTransList.get(0).getPrice();
            endPrice = todayTransList.get(todayTransList.size()-1).getPrice();
            minPrice = todayTransList.get(0).getPrice();
            maxPrice = todayTransList.get(0).getPrice();
            for(Transaction t : todayTransList){
                float transPrice = t.getPrice();
                if(transPrice > maxPrice) maxPrice = transPrice;
                if(transPrice < minPrice) minPrice = transPrice;
            }
            todayPrices.setStartPrice(startPrice);
            todayPrices.setEndPrice(endPrice);
            todayPrices.setMaxPrice(maxPrice);
            todayPrices.setMinPrice(minPrice);
        }
        return todayPrices;
    }

    public PriceInfo getYesterdayPrices(){
        //LocalDate yesterday = LocalDate.now().minusDays(1);
        int yesterday = clock.getDay() - 1;
        PriceInfo yesterdayPrices = new PriceInfo();
        if(transactions.containsKey(yesterday)){
            List<Transaction> todayTransList = transactions.get(yesterday);
            float startPrice, endPrice, maxPrice = 0, minPrice = 0;
            startPrice = todayTransList.get(0).getPrice();
            endPrice = todayTransList.get(todayTransList.size()-1).getPrice();
            minPrice = todayTransList.get(0).getPrice();
            maxPrice = todayTransList.get(0).getPrice();
            for(Transaction t : todayTransList){
                float transPrice = t.getPrice();
                if(transPrice > maxPrice) maxPrice = transPrice;
                if(transPrice < minPrice) minPrice = transPrice;
            }
            yesterdayPrices.setStartPrice(startPrice);
            yesterdayPrices.setEndPrice(endPrice);
            yesterdayPrices.setMaxPrice(maxPrice);
            yesterdayPrices.setMinPrice(minPrice);
        }
        return yesterdayPrices;
    }
}
