package exchange;

public abstract class Order{

    private String id;
    private String company_id;
    private int quantity;
    private float price;

    public Order(){}

    public Order(String id, String company_id, int quantity, float price){
        this.id = id;
        this.company_id = company_id;
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
        return company_id;
    }
}
