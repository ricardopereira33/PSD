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
public class Sell extends Order {
    private String seller;

    public Sell(){}

    public Sell(String seller, String company_id, int quantity, float price){
        super(company_id,quantity,price);
        this.seller = seller;
    }

    public String getSeller(){
        return this.seller;
    }
}