package com.nexonsalary.config;

import com.nexonsalary.model.Agent;
import com.nexonsalary.model.CommissionTransaction;
import com.nexonsalary.model.Employee;
import com.nexonsalary.model.Member;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/nexon_salary");
                configuration.setProperty("hibernate.connection.username", "root");
                configuration.setProperty("hibernate.connection.password", "12345678");
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                configuration.setProperty("hibernate.show_sql", "true");
                configuration.setProperty("hibernate.format_sql", "true");
                configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(Agent.class);
                configuration.addAnnotatedClass(Member.class);
                configuration.addAnnotatedClass(MonthlyMemberBalance.class);
                configuration.addAnnotatedClass(CommissionTransaction.class);

                StandardServiceRegistryBuilder builder =
                        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());

                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to build SessionFactory");
            }
        }

        return sessionFactory;
    }
}