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
public abstract class Order{
    private String company_id;
    private int quantity;
    private float price;

    public Order(){}

    public Order(String company_id, int quantity, float price){
        this.company_id = company_id;
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity(){
        return quantity;
    }

    public float getPrice(){
        return price;
    }

    public String getCompany(){
        return company_id;
    }
}