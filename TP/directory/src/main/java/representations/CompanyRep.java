package representations;

import com.fasterxml.jackson.annotation.*;

public class CompanyRep {
    public final String name;
    public final String host;
    public final String port;
    public final String description;
    public final String exchange;

    @JsonCreator
    public CompanyRep(@JsonProperty("name") String name, @JsonProperty("host") String host, @JsonProperty("port") String port, @JsonProperty("description") String description, @JsonProperty("exchange") String exchange){
        this.name = name;
        this.host = host;
        this.port = port;
        this.description = description;
        this.exchange = exchange;
    }
}
