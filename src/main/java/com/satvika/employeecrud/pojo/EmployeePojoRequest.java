package com.satvika.employeecrud.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmployeePojoRequest {

    @NotNull(message = "First name should not be blank")
    private String firstName;

    @NotNull(message = "Last name should not be blank")
    private String lastName;

    @Email(message = "Email should be entered in correct format")
    @NotNull(message = "Email should not be blank")
    private String email;

    @NotNull(message = "Address should not be blank")
    private String address;

    @Pattern(regexp = "^[0-9]{10}$",message = "phone number should be of 10 digits")
    private String phoneNumber;

    @NotNull(message = "Position should not be blank")
    private String position;

    @NotNull(message = "Salary must be greater than 0")
    private double salary;

    @NotNull(message = "File url is required")
    private String fileUrl;

}
