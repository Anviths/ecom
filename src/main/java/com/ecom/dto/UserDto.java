package com.ecom.dto;

import com.ecom.entity.Role;
import com.ecom.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Set<Role> roles;
    private LocalDateTime createAt;

    public static UserDto from(User user) {

            return UserDto.builder()
                    .id(user.getUserId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .createAt(user.getCreatedAt())
                    .roles(user.getRoles())
                    .build();
        }

}
