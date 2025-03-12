package com.satvika.employeecrud.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.entity.Employee;
import com.satvika.employeecrud.pojo.DocumentResponsePojo;
import com.satvika.employeecrud.pojo.EmployeePojoRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import com.satvika.employeecrud.repository.DocumentRepository;
import com.satvika.employeecrud.repository.EmployeeRepository;
import com.satvika.employeecrud.service.EmployeeService;
import com.satvika.employeecrud.util.ExcelGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DocumentRepository documentRepository,
                               ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public EmployeePojoResponse createEmployee(MultipartFile file, String employeeJson) {
        try {
            EmployeePojoRequest request = objectMapper.readValue(employeeJson, EmployeePojoRequest.class);
            String fileUrl = saveFile(file);
            String fileName = file.getOriginalFilename();

            request.setFileUrl(fileUrl);

            Employee employee = new Employee();
            employee.setFirstName(request.getFirstName());
            employee.setLastName(request.getLastName());
            employee.setEmail(request.getEmail());
            employee.setAddress(request.getAddress());
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setPosition(request.getPosition());
            employee.setSalary(request.getSalary());

            Employee savedEmployee = employeeRepository.save(employee);

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileUrl(fileUrl);
            document.setEmployee(savedEmployee);
            documentRepository.save(document);

            return mapToResponse(savedEmployee);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse employee JSON", e);
        }
    }

    @Override
    public EmployeePojoResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    @Override
    public List<EmployeePojoResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeePojoResponse updateEmployee(Long id, EmployeePojoRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setAddress(request.getAddress());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public void exportEmployeesToExcel() {
        List<Employee> employees = employeeRepository.findAll();
        try {
            byte[] excelData = ExcelGenerator.generateExcel(employees);
            Path exportPath = Paths.get("exports", "employees_with_documents.xlsx");
            Files.createDirectories(exportPath.getParent());
            Files.write(exportPath, excelData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }

    private String saveFile(MultipartFile file) {
        String uploadDir = "uploads/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }

        return "http://localhost:8080/uploads/" + fileName;
    }

    private EmployeePojoResponse mapToResponse(Employee employee) {
        EmployeePojoResponse response = new EmployeePojoResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAddress(),
                employee.getPhoneNumber(),
                employee.getPosition(),
                employee.getSalary()
        );

        if (employee.getDocuments() != null) {
            List<DocumentResponsePojo> documentPojos = employee.getDocuments().stream()
                    .map(doc -> new DocumentResponsePojo(doc.getFileName(), doc.getFileUrl()))
                    .collect(Collectors.toList());
            response.setDocuments(documentPojos);
        }

        return response;
    }
}
