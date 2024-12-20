package com.swiftpos.swiftposauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserResponse {
    private UUID userId;
    private UUID userDetailId;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private LocalDate birthday;
    private String imageUrl;
}
