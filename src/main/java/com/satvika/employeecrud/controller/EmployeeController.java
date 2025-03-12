package com.satvika.employeecrud.controller;

import com.satvika.employeecrud.pojo.EmployeePojoRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import com.satvika.employeecrud.service.EmployeeService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<EmployeePojoResponse> createEmployee(
            @RequestPart("file") MultipartFile file,
            @RequestPart("employee") String employeeJson) {
        return ResponseEntity.ok(employeeService.createEmployee(file, employeeJson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeePojoResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeePojoResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeePojoResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeePojoRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    @GetMapping("/export/excel")
    public ResponseEntity<String> triggerExcelExport() {
        employeeService.exportEmployeesToExcel();
        return ResponseEntity.ok("Employee data exported to Excel successfully.");
    }

    @GetMapping("/download/excel")
    public ResponseEntity<FileSystemResource> downloadExcel() {
        String filePath = "exports/employees_with_documents.xlsx";
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=employees_with_documents.xlsx")
                .body(resource);
    }

}
