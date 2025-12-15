package com.ecom.service.util;

import com.ecom.dto.LoginRequest;
import com.ecom.dto.RegisterRequest;
import com.ecom.entity.RefreshToken;
import com.ecom.entity.Role;
import com.ecom.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;

public class TestData {

    public static RegisterRequest registerRequest() {
        RegisterRequest r = new RegisterRequest();
        r.setName("Test User");
        r.setEmail("test@example.com");
        r.setPassword("Password@123");
        r.setPhone("+919999999999");
        r.setDateOfBirth(LocalDate.of(1999, 1, 1));
        return r;
    }

    public static LoginRequest loginRequest() {
        LoginRequest r = new LoginRequest();
        r.setEmail("test@example.com");
        r.setPassword("Password@123");
        return r;
    }

    public static User user() {
        return User.builder()
                .userId(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encoded")
                .roles(EnumSet.of(Role.USER))
                .build();
    }

    public static RefreshToken refreshToken(User user) {
        return RefreshToken.builder()
                .token("refresh-token")
                .user(user)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();
    }
}

