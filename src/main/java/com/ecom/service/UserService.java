package com.ecom.service;

import com.ecom.dto.RegisterRequest;
import com.ecom.dto.UpdatePassword;
import com.ecom.dto.UserDto;
import com.ecom.dto.UserUpdateRequest;
import com.ecom.entity.User;
import jakarta.validation.Valid;

public interface UserService {
    User createUser(RegisterRequest request);
    User loadUserByEmail(String email);

    UserDto updateProfile(String email, UserUpdateRequest userUpdateRequest);

    void changePassword(String email,  UpdatePassword updatePassword);
}
