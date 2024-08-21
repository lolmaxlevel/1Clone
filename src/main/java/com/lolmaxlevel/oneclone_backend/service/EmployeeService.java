package com.lolmaxlevel.oneclone_backend.service;

import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void saveAll(List<Employee> employees) {
        employeeRepository.saveAll(employees);
    }
}