package com.nexonsalary.repository;
import com.nexonsalary.util.HibernateUtil;
import com.nexonsalary.model.Employee;
import org.hibernate.Session;
import java.util.List;

public class EmployeeRepository {

    public List<Employee> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Employee", Employee.class).list();
        }
    }
}