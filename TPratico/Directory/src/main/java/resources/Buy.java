package resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Buy extends Order {
    private String buyer;

    public Buy(){}

    public Buy(String buyer, String company_id, int quantity, float price){
        super(company_id,quantity,price);
        this.buyer = buyer;
    }

    public String getBuyer(){
        return this.buyer;
    }
}
