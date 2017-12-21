package resources;

public class Transaction {
    private String id;
    private Sell sellOrder;
    private Buy buyOrder;
    private float price;
    private String company;

    public Transaction(){ }

    public Transaction(String id, Sell sellOrder, Buy buyOrder, float price, String company){
        this.id = id;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;
        this.price = price;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public Sell getSellOrder() {
        return sellOrder;
    }

    public Buy getBuyOrder() {
        return buyOrder;
    }

    public float getPrice() {
        return price;
    }

    public String getCompany() {
        return company;
    }
}
