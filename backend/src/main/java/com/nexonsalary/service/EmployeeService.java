package com.nexonsalary.service;

import com.nexonsalary.model.Employee;
import com.nexonsalary.repository.EmployeeRepository;

import java.util.List;

public class EmployeeService {

    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}