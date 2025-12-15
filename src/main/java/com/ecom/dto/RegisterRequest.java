package com.ecom.dto;

import com.ecom.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    @Email
    @NotBlank private String email;
    @NotBlank private String password;
    private String phone;
    private LocalDate dateOfBirth;

}
