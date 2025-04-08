package com.satvika.employeecrud.util;

import com.satvika.employeecrud.pojo.EmployeePojoResponse;
import com.satvika.employeecrud.pojo.DocumentResponsePojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class CsvGenerator {

    public static ByteArrayInputStream generateCsv(List<EmployeePojoResponse> employees) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        writer.println("ID,First Name,Last Name,Email,Address,Phone,Position,Salary,File Name(s),File URL(s)");

        for (EmployeePojoResponse emp : employees) {
            String fileNames = "";
            String fileUrls = "";

            if (emp.getDocuments() != null && !emp.getDocuments().isEmpty()) {
                fileNames = emp.getDocuments().stream()
                        .map(DocumentResponsePojo::getFileName)
                        .collect(Collectors.joining(" | "));

                fileUrls = emp.getDocuments().stream()
                        .map(DocumentResponsePojo::getFileUrl)
                        .collect(Collectors.joining(" | "));
            }

            writer.printf("%d,%s,%s,%s,%s,%s,%s,%.2f,%s,%s%n",
                    emp.getId(),
                    escape(emp.getFirstName()),
                    escape(emp.getLastName()),
                    escape(emp.getEmail()),
                    escape(emp.getAddress()),
                    escape(emp.getPhoneNumber()),
                    escape(emp.getPosition()),
                    emp.getSalary(),
                    escape(fileNames),
                    escape(fileUrls)
            );
        }

        writer.flush();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private static String escape(String input) {
        if (input == null) return "";
        return "\"" + input.replace("\"", "\"\"") + "\"";
    }
}
