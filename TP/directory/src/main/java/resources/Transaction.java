package resources;

public class Transaction {
    private String id;
    private String sellOrder;
    private String buyOrder;
    private int quantity;
    private float price;
    private String company;

    public Transaction(){ }

    public Transaction(String id, String sellOrder, String buyOrder, int quantity, float price, String company){
        this.id = id;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;
        this.quantity = quantity;
        this.price = price;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public String getSellOrder() {
        return sellOrder;
    }

    public String getBuyOrder() {
        return buyOrder;
    }

    public float getPrice() {
        return price;
    }

    public String getCompany() {
        return company;
    }

    public int getQuantity(){
        return quantity;
    }
}
