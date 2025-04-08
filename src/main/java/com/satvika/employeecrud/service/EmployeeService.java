package com.satvika.employeecrud.service;

import com.satvika.employeecrud.entity.Employee;
import com.satvika.employeecrud.pojo.EmployeePojoRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.satvika.employeecrud.pojo.EmployeeFormRequest;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

    EmployeePojoResponse createEmployee(MultipartFile file, String employeeJson);

    EmployeePojoResponse getEmployeeById(Long id);

    List<EmployeePojoResponse> getAllEmployees();

   EmployeePojoResponse updateEmployee(Long id, MultipartFile file, EmployeePojoRequest request);


    List<Employee> getAllActiveEmployees();

    void deleteEmployee(Long id);

    @Transactional
    void restoreEmployee(Long id);

    @Transactional
    void hardDeleteEmployee(Long id);

    void exportEmployeesToExcel();

    String saveOrUpdateFromForm(EmployeeFormRequest request);

    void exportCsv(HttpServletResponse response) throws IOException;

    Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDir);

    Page<Employee> findPaginatedActiveEmployees(int pageNo, int pageSize, String sortField, String sortDir);
}
