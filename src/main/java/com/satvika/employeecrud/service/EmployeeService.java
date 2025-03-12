package com.satvika.employeecrud.service;

import com.satvika.employeecrud.pojo.EmployeePojoRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    EmployeePojoResponse createEmployee(MultipartFile file, String employeeJson);

    EmployeePojoResponse getEmployeeById(Long id);

    List<EmployeePojoResponse> getAllEmployees();

    EmployeePojoResponse updateEmployee(Long id, EmployeePojoRequest request);

    void deleteEmployee(Long id);

    void exportEmployeesToExcel();
}
