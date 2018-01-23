package resources;

public class Buy extends Order {
    private String buyer;

    public Buy(){}

    public Buy(String id, String buyer, String company, int quantity, float price){
        super(id, company, quantity, price);
        this.buyer = buyer;
    }

    public String getBuyer(){
        return this.buyer;
    }
}
