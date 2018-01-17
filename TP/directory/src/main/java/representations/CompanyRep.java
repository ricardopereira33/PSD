package representations;

import com.fasterxml.jackson.annotation.*;

public class CompanyRep {
    public final String id;
    public final String name;
    public final String exange;
    public final String description;

    @JsonCreator
    public CompanyRep(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("exange") String exange, @JsonProperty("description") String description){
        this.id = id;
        this.name = name;
        this.exange = exange;
        this.description = description;
    }
}
