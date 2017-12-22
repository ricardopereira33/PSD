import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import health.TemplateHealthCheck;
import resources.CompanyResource;

public class DirectoryApp extends Application<DirectoryAppConfiguration>{
    public static void main(String[] args) throws Exception {
        new DirectoryApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<DirectoryAppConfiguration> bootstrap) { }

    @Override
    public void run(DirectoryAppConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new CompanyResource());
        environment.healthChecks().register("template",
                new TemplateHealthCheck(""));
    }
}
