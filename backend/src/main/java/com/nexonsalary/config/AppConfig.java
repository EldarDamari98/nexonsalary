package com.nexonsalary.config;
import com.nexonsalary.controller.CommissionController;
import com.nexonsalary.controller.DashboardController;
import com.nexonsalary.controller.AgentController;
import com.nexonsalary.controller.BalanceImportController;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(AgentController.class);
        register(BalanceImportController.class);
        register(CommissionController.class);
        register(MultiPartFeature.class);
        register(CorsFilter.class);
        register(DashboardController.class);
        packages("org.glassfish.jersey.jackson");
    }
}