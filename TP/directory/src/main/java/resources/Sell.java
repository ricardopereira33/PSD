package resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Sell extends Order {
    private String seller;

    public Sell(){}

    public Sell(String id, String seller, String company_id, int quantity, float price){
        super(id, company_id, quantity, price);
        this.seller = seller;
    }

    public String getSeller(){
        return this.seller;
    }
}
