package com.ecom.controller;

import com.ecom.dto.UpdatePassword;
import com.ecom.dto.UserDto;
import com.ecom.dto.UserUpdateRequest;
import com.ecom.entity.User;
import com.ecom.exception.TooManyRequestsException;
import com.ecom.service.UserService;
import com.ecom.service.impl.PasswordRateLimter;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecom/user")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;
    private final PasswordRateLimter passwordRateLimter;
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
       String name= authentication.getName();
        System.out.println(name);
      User user= userService.loadUserByEmail(name);

       UserDto userDto=UserDto.builder()
               .id(user.getUserId())
               .name(user.getName())
               .phone(user.getPhone())
               .email(user.getEmail())
               .roles(user.getRoles())
               .createAt(user.getCreatedAt())
               .build();
        return ResponseEntity.ok(userDto);


    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest, Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }
        String email= authentication.getName();
        UserDto userDto=userService.updateProfile(email,userUpdateRequest);

        return ResponseEntity.ok(userDto);

    }
    @PutMapping("/change-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePassword updatePassword,Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }
        String email=authentication.getName();
        Bucket bucket=passwordRateLimter.resolveBucket(email);
        if(!bucket.tryConsume(1)){
            throw new TooManyRequestsException("Too many password change attempts");
        }
        userService.changePassword(email,updatePassword);
       return ResponseEntity.noContent().build();
    }
}
