package com.satvika.employeecrud.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.entity.Employee;
import com.satvika.employeecrud.pojo.DocumentResponsePojo;
import com.satvika.employeecrud.pojo.EmployeeFormRequest;
import com.satvika.employeecrud.pojo.EmployeePojoRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import com.satvika.employeecrud.repository.DocumentRepository;
import com.satvika.employeecrud.repository.EmployeeRepository;
import com.satvika.employeecrud.service.EmployeeService;
import com.satvika.employeecrud.util.CsvGenerator;
import com.satvika.employeecrud.util.ExcelGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    /*public EmployeePojoResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        return EmployeePojoResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .phoneNumber(employee.getPhoneNumber())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .build();
    }*/
    @Override
    public EmployeePojoResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .filter(emp -> !emp.isDeleted())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeePojoResponse response = EmployeePojoResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .phoneNumber(employee.getPhoneNumber())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .build();

        List<DocumentResponsePojo> documentResponseList = employee.getDocuments().stream()
                .map(document -> new DocumentResponsePojo(document.getFileName(), document.getFileUrl()))
                .collect(Collectors.toList());

        response.setDocuments(documentResponseList);

        return mapToResponse(employee);
    }

    @Override
    public List<EmployeePojoResponse> getAllEmployees() {
        return employeeRepository.findByDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeePojoResponse updateEmployee(Long id, MultipartFile file, EmployeePojoRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setAddress(request.getAddress());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());

        if (file != null && !file.isEmpty()) {
            String fileUrl = saveFile(file);
            String fileName = file.getOriginalFilename();

            documentRepository.deleteByEmployee(employee);

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileUrl(fileUrl);
            document.setEmployee(employee);
            documentRepository.save(document);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public String saveOrUpdateFromForm(EmployeeFormRequest request) {
        try {
            if (request.getId() == null) {

                EmployeePojoRequest createReq = EmployeePojoRequest.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .address(request.getAddress())
                        .phoneNumber(request.getPhoneNumber())
                        .position(request.getPosition())
                        .salary(request.getSalary())
                        .build();

                String json = objectMapper.writeValueAsString(createReq);
                createEmployee(request.getFile(), json);
                return "created";

            } else {
                EmployeePojoRequest updateReq = EmployeePojoRequest.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .address(request.getAddress())
                        .phoneNumber(request.getPhoneNumber())
                        .position(request.getPosition())
                        .salary(request.getSalary())
                        .build();

                updateEmployee(request.getId(), request.getFile(), updateReq);
                return "updated";
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save/update employee via form", e);
        }
    }

    @Override
    public List<Employee> getAllActiveEmployees() {
        return employeeRepository.findByDeletedFalse();
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setDeleted(true);
        employeeRepository.save(employee);
    }
    @Transactional
    @Override
    public void restoreEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setDeleted(false);
        employeeRepository.save(employee);
    }

    @Transactional
    @Override
    public void hardDeleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepository.delete(employee);
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

    private List<DocumentResponsePojo> documentMapper(List<Document> documents) {
        if (documents == null) {
            return new ArrayList<>();
        }

        return documents.stream()
                .map(doc -> new DocumentResponsePojo(doc.getFileName(), doc.getFileUrl()))
                .collect(Collectors.toList());
    }

    private EmployeePojoResponse mapToResponse(Employee employee) {
        return EmployeePojoResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .phoneNumber(employee.getPhoneNumber())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .documents(documentMapper(employee.getDocuments()))
                .build();
    }
    @Override
    public void exportCsv(HttpServletResponse response) throws IOException {
        List<EmployeePojoResponse> employees = getAllEmployees();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=employees.csv");

        ByteArrayInputStream csvStream = CsvGenerator.generateCsv(employees);
        csvStream.transferTo(response.getOutputStream());

        response.flushBuffer();
    }

    @Override
    public Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, 3, sort);
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Page<Employee> findPaginatedActiveEmployees(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return employeeRepository.findByDeletedFalse(pageable);
    }

}
