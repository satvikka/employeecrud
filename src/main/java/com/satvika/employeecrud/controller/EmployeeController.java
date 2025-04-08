package com.satvika.employeecrud.controller;

import com.satvika.employeecrud.entity.Employee;
import com.satvika.employeecrud.pojo.EmployeeFormRequest;
import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import com.satvika.employeecrud.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            EmployeePojoResponse emp = employeeService.getEmployeeById(id);
            model.addAttribute("employeeForm", EmployeeFormRequest.builder()
                    .id(emp.getId())
                    .firstName(emp.getFirstName())
                    .lastName(emp.getLastName())
                    .email(emp.getEmail())
                    .address(emp.getAddress())
                    .phoneNumber(emp.getPhoneNumber())
                    .position(emp.getPosition())
                    .salary(emp.getSalary())
                    .build());
        } else {
            model.addAttribute("employeeForm", new EmployeeFormRequest());
        }
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "form";
    }


    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employeeForm") EmployeeFormRequest request,
                               RedirectAttributes redirectAttributes) {
        String result = employeeService.saveOrUpdateFromForm(request);
        redirectAttributes.addFlashAttribute("success", "Employee " + result + " successfully.");
        return "redirect:/employees/employee-list";

    }

    @GetMapping("/employee-list")
    public String showEmployeeList(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "sortField", defaultValue = "firstName") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        int pageSize = 3;

        Page<Employee> page = employeeService.findPaginatedActiveEmployees(pageNo, pageSize, sortField, sortDir);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("employees", listEmployees);

        return "employee-list";
    }

    @GetMapping("/list")
    public String showEmployeeList(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employee-list";
    }
    @GetMapping("/employee/{id}")
    public String getEmployeeById(@PathVariable Long id, Model model) {
        EmployeePojoResponse response = employeeService.getEmployeeById(id);
        model.addAttribute("employee", response);
        return "employee-details";
    }

    @GetMapping("/hard-delete/{id}")
    public String hardDeleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.hardDeleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee permanently deleted.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employees/employee-list";
    }

    @GetMapping("/restore-form")
    public String showRestoreForm() {
        return "restore-form";
    }

    @PostMapping("/restore")
    public String restoreEmployee(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.restoreEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee restored successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found.");
        }
        return "redirect:/employees/employee-list";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee marked as deleted.");
        return "redirect:/employees/employee-list";
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee marked as deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/hard-delete")
    public ResponseEntity<String> hardDeleteEmployee(@PathVariable Long id) {
        employeeService.hardDeleteEmployee(id);
        return ResponseEntity.ok("Employee permanently deleted");
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<String> restoreEmployee(@PathVariable Long id) {
        try {
            employeeService.restoreEmployee(id);
            return ResponseEntity.ok("Employee restored successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }*/


    @GetMapping("/export/excel")
    public String exportExcel(RedirectAttributes redirectAttributes) {
        employeeService.exportEmployeesToExcel();
        redirectAttributes.addFlashAttribute("success", "Excel exported.");
        return "redirect:/employees/list";
    }
    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        employeeService.exportCsv(response);
    }


    @GetMapping("/download/excel")
    public ResponseEntity<FileSystemResource> downloadExcel() {
        String path = "exports/employees_with_documents.xlsx";
        File file = new File(path);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=employees_with_documents.xlsx")
                .body(new FileSystemResource(file));
    }
}

