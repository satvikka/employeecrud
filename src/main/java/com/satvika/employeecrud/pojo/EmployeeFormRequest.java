package com.satvika.employeecrud.pojo;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeFormRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String position;
    private double salary;
    private MultipartFile file;
    private String fileUrl;
    private List<DocumentResponsePojo> documents;
}
