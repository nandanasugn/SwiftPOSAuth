package com.swiftpos.swiftposauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 6, max = 32, message = "username length min 6, max 32")
    @Schema(description = "The username of the user whose password is being changed", example = "john_doe", minLength = 6, maxLength = 32)
    private String username;

    @Schema(description = "Full name of the user", example = "John Wick")
    @Size(max = 100, message = "max first name is 100 characters")
    private String fullName;

    @Schema(description = "Email address of the user", example = "johndoe@example.com")
    @Email(message = "email is invalid format")
    private String email;

    @Schema(description = "Address of the user", example = "123 Main Street, Jakarta")
    private String address;

    @Schema(description = "Phone number of the user. Must start with +62 or 08 and contain 10 to 15 digits", example = "+628123456789")
    @Pattern(
            regexp = "^\\+(62)[ ]?[0-9]{9,13}$|^0[8][0-9]{9,12}$",
            message = "Phone number must be valid and contain 10 to 15 digits"
    )
    private String phone;

    @Schema(description = "Date of birth of the user. Must be in the past", example = "1990-01-01")
    @Past(message = "date of Birth must be in the past")
    private LocalDate birthday;

    @Schema(description = "Image of the user. Must be in jpg, jpeg, or png format")
    private MultipartFile file;
}
