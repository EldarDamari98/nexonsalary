package com.nexonsalary.config;

import com.nexonsalary.controller.BalanceImportController;
import com.nexonsalary.controller.EmployeeController;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(EmployeeController.class);
        register(BalanceImportController.class);
        register(MultiPartFeature.class);
        register(CorsFilter.class);
        packages("org.glassfish.jersey.jackson");
    }
}