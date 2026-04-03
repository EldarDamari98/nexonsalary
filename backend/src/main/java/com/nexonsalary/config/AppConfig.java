package com.nexonsalary.config;

import com.nexonsalary.controller.EmployeeController;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(EmployeeController.class);
        register(CorsFilter.class);
        packages("org.glassfish.jersey.jackson");
    }
}