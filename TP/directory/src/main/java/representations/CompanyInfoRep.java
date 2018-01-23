package representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyInfoRep {
    public String company;
    public String exchange;
    public String host;
    public String port;
    public float todayStartPrice;
    public float todayEndPrice;
    public float todayMaxPrice;
    public float todayMinPrice;
    public float yesterdayStartPrice;
    public float yesterdayEndPrice;
    public float yesterdayMaxPrice;
    public float yesterdayMinPrice;

    @JsonCreator
    public CompanyInfoRep(@JsonProperty("Company") String company,
                          @JsonProperty("Exchange") String exchange,
                          @JsonProperty("Host") String host,
                          @JsonProperty("Port") String port,
                          @JsonProperty("TodayStartPrice") float todayStartPrice,
                          @JsonProperty("TodayEndPrice") float todayEndPrice,
                          @JsonProperty("TodayMinPrice") float todayMinPrice,
                          @JsonProperty("TodayMaxPrice") float todayMaxPrice,
                          @JsonProperty("YesterdayStartPrice") float yesterdayStartPrice,
                          @JsonProperty("YesterdayEndPrice") float yesterdayEndPrice,
                          @JsonProperty("YesterdayMinPrice") float yesterdayMinPrice,
                          @JsonProperty("YesterdayMaxPrice") float yesterdayMaxPrice){

        this.company = company;
        this.exchange = exchange;
        this.host = host;
        this.port = port;
        this.todayStartPrice = todayStartPrice;
        this.todayEndPrice = todayEndPrice;
        this.todayMaxPrice = todayMaxPrice;
        this.todayMinPrice = todayMinPrice;
        this.yesterdayStartPrice = yesterdayStartPrice;
        this.yesterdayEndPrice = yesterdayEndPrice;
        this.yesterdayMaxPrice = yesterdayMaxPrice;
        this.yesterdayMinPrice = yesterdayMinPrice;
    }
}
