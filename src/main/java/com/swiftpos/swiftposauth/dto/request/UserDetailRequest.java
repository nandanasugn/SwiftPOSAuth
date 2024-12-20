package com.swiftpos.swiftposauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailRequest {

    private String fullName;

    private String email;

    private String address;

    private String phone;

    private LocalDate birthday;
    
    private MultipartFile file;

    private UUID userId;
}
