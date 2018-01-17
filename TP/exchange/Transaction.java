/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

/**
 *
 * @author dinispeixoto
 */
public class Transaction {
    private String id;
    private String sellOrder;
    private String buyOrder;
    private float price;
    private int quantity;
    private String company;

    public Transaction(){ }

    public Transaction(String id, String sellOrder, String buyOrder, float price, int quantity, String company){
        this.id = id;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;
        this.price = price;
        this.quantity = quantity;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public String getSellOrder() {
        return sellOrder;
    }

    public String getBuyOrder() {
        return buyOrder;
    }

    public float getPrice() {
        return price;
    }

    public String getCompany() {
        return company;
    }

    public int getQuantity() {
        return quantity;
    }
    
    
}
