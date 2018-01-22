package representations;

import com.fasterxml.jackson.annotation.*;

public class CompanyRep {
    public final String id;
    public final String name;
    public final String host;
    public final String port;
    public final String description;

    @JsonCreator
    public CompanyRep(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("host") String host, @JsonProperty("port") String port, @JsonProperty("description") String description){
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.description = description;
    }
}
