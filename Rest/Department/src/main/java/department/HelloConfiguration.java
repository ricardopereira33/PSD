package department;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class HelloConfiguration extends Configuration {
    @NotEmpty
    public String template;

    public String defaultName = "Stranger";
}

