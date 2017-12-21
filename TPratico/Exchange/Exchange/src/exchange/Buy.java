package exchange;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dinispeixoto
 */
public class Buy extends Order {
    private User buyer;

    public Buy(){}

    public Buy(User buyer, String company_id, int quantity, float price){
        super(company_id,quantity,price);
        this.buyer = buyer;
    }

    public User getBuyer(){
        return this.buyer;
    }
}
