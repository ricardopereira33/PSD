package representations;

import com.fasterxml.jackson.annotation.*;

public class PriceInfo {
    public float startPrice;
    public float endPrice;
    public float maxPrice;
    public float minPrice;

    @JsonCreator
    public PriceInfo(@JsonProperty("StartPrice") float startPrice, @JsonProperty("EndPrice") float endPrice, @JsonProperty("MinPrice") float minPrice, @JsonProperty("MaxPrice") float maxPrice){
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

}
