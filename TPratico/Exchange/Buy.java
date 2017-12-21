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