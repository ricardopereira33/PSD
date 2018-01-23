package resources;

public class PriceInfo {
    public float startPrice;
    public float endPrice;
    public float maxPrice;
    public float minPrice;

    public PriceInfo(){
        startPrice = 0;
        endPrice = 0;
        minPrice = 0;
        maxPrice = 0;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public float getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(float endPrice) {
        this.endPrice = endPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
}
