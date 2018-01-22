package exchange;

public class Sell extends Order {

    private String seller;

    public Sell(){}

    public Sell(String id, String seller, String company, int quantity, float price){
        super(id, company, quantity, price);
        this.seller = seller;
    }

    public String getSeller(){
        return this.seller;
    }
}