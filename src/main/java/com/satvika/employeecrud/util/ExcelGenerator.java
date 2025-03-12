package com.satvika.employeecrud.util;

import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.entity.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static byte[] generateExcel(List<Employee> employees) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees with Documents");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "First Name", "Last Name", "Email", "Address", "Phone", "Position", "Salary", "File Name(s)", "File URL(s)"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getFirstName());
            row.createCell(2).setCellValue(employee.getLastName());
            row.createCell(3).setCellValue(employee.getEmail());
            row.createCell(4).setCellValue(employee.getAddress());
            row.createCell(5).setCellValue(employee.getPhoneNumber());
            row.createCell(6).setCellValue(employee.getPosition());
            row.createCell(7).setCellValue(employee.getSalary());

            String fileNames = "";
            String fileUrls = "";
            if (employee.getDocuments() != null && !employee.getDocuments().isEmpty()) {
                fileNames = String.join(", ",
                        employee.getDocuments().stream()
                                .map(Document::getFileName)
                                .toArray(String[]::new));

                fileUrls = String.join(", ",
                        employee.getDocuments().stream()
                                .map(Document::getFileUrl)
                                .toArray(String[]::new));
            }

            row.createCell(8).setCellValue(fileNames);
            row.createCell(9).setCellValue(fileUrls);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
