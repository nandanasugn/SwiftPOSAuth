package com.swiftpos.swiftposauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRequest {

    private String username;

    private String password;

    @NotNull(message = "Role cannot be null")
    @Pattern(regexp = "^(OWNER|ADMIN|STAFF)$", message = "Invalid role. Allowed roles are: OWNER, ADMIN, STAFF")
    @Schema(description = "Role of the user", example = "OWNER, ADMIN, or STAFF")
    private String role;
}
