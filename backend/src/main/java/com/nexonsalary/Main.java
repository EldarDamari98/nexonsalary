package com.nexonsalary;

import com.nexonsalary.config.AppConfig;
import com.nexonsalary.util.HibernateUtil;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import com.nexonsalary.util.HibernateUtil;

import java.net.URI;

public class Main {

    public static void main(String[] args) {
        HibernateUtil.getSessionFactory();

        URI uri = URI.create("http://localhost:8080/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, new AppConfig());

        System.out.println("Server started at http://localhost:8080");
        System.out.println("Employees API: http://localhost:8080/employees");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdownNow();
            HibernateUtil.shutdown();
        }));
    }
}