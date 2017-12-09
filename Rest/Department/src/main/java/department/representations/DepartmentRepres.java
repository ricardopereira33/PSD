package department.representations;

import com.fasterxml.jackson.annotation.*;

public class DepartmentRepres {
    public final String name;
    public final String descrip;

    @JsonCreator
    public DepartmentRepres(@JsonProperty("name") String name, @JsonProperty("description") String descrip ) {
        this.name = name;
        this.descrip = descrip;
    }
}

