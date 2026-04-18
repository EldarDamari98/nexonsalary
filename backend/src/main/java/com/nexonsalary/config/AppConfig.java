package com.nexonsalary.config;

import com.nexonsalary.controller.AgentController;
import com.nexonsalary.controller.BalanceImportController;
import com.nexonsalary.controller.CommissionController;
import com.nexonsalary.controller.DashboardController;
import com.nexonsalary.controller.StatisticsController;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(AgentController.class);
        register(BalanceImportController.class);
        register(CommissionController.class);
        register(DashboardController.class);
        register(StatisticsController.class);
        register(MultiPartFeature.class);
        register(CorsFilter.class);
        packages("org.glassfish.jersey.jackson");
    }
}