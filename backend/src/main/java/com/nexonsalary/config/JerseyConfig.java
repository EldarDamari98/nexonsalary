package com.nexonsalary.config;

import com.nexonsalary.controller.BalanceImportController;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(BalanceImportController.class);
        register(MultiPartFeature.class);
    }
}