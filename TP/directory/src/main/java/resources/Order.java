package resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Order{

    private String id;
    @JsonProperty("company")
    private String company;
    private int quantity;
    private float price;

    public Order(){}

    public Order(String id, String company, int quantity, float price){
        this.id = id;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId(){
        return id;
    }

    public int getQuantity(){
        return quantity;
    }

    public float getPrice(){
        return price;
    }

    public String getCompany(){
        return company;
    }
}
