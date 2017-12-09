package department;

import department.health.TemplateHealthCheck;
import department.resources.Department;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloApplication extends Application<HelloConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloApplication().run(args);
    }

    @Override
    public String getName() { return "Hello"; }

    @Override
    public void initialize(Bootstrap<HelloConfiguration> bootstrap) { }

    @Override
    public void run(HelloConfiguration configuration,
                    Environment environment) {
        environment.jersey().register(
            new Department(configuration.template, configuration.defaultName));
        environment.healthChecks().register("template",
            new TemplateHealthCheck(configuration.template));
    }

}

