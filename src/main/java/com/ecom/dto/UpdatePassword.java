package com.ecom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePassword {


    @NotBlank
    private String oldPassword;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+&]{8,32}"
    ,message = "Password must be 8-32 chars, include upper, lower, number and special character")
    private String newPassword;
}
