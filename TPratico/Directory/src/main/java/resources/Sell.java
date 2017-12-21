package resources;

import resources.Order;

public class Sell extends Order {
    private User seller;

    public Sell(){}

    public Sell(User seller, String company_id, int quantity, float price){
        super(company_id,quantity,price);
        this.seller = seller;
    }

    public User getSeller(){
        return this.seller;
    }
}
