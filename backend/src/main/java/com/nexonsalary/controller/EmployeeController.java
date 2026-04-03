package com.nexonsalary.controller;

import com.nexonsalary.model.Employee;
import com.nexonsalary.service.EmployeeService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/employees")
public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}