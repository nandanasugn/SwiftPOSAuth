package com.swiftpos.swiftposauth.controller;

import com.swiftpos.swiftposauth.dto.request.LoginRequest;
import com.swiftpos.swiftposauth.dto.request.RegisterRequest;
import com.swiftpos.swiftposauth.dto.response.CommonResponse;
import com.swiftpos.swiftposauth.dto.response.LoginResponse;
import com.swiftpos.swiftposauth.exception.CustomException;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.service.IAuthService;
import com.swiftpos.swiftposauth.service.IUserService;
import com.swiftpos.swiftposauth.util.MapperUtil;
import com.swiftpos.swiftposauth.util.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final ObjectValidator objectValidator;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/owner/register")
    @Operation(
            summary = "Create admin or staff account",
            description = "Create new account for admin or staff.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "registerRequest", description = "Register request form", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> registerUser(@ModelAttribute RegisterRequest registerRequest) {
        objectValidator.validate(registerRequest);

        String password = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(password);

        authService.register(registerRequest);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @PostMapping("/register/staff")
    @Operation(
            summary = "Create staff account",
            description = "Create new account for staff.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "registerRequest", description = "Register request form", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> registerStaff(@ModelAttribute RegisterRequest registerRequest) {
        objectValidator.validate(registerRequest);

        String password = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(password);

        if (registerRequest.getRole().equals("STAFF")) {
            authService.register(registerRequest);
        } else {
            throw new CustomException("Cannot register with this role", HttpStatus.UNAUTHORIZED);
        }

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

   // @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'STAFF')")
    @PutMapping("/change-password")
    @Operation(
            summary = "Change password",
            description = "Change user's current password.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "User's id", required = true),
                    @Parameter(name = "oldPassword", description = "User's old password", required = true),
                    @Parameter(name = "newPassword", description = "User's new password", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> changePassword(@RequestParam("id") UUID id, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {

        User user = userService.findById(id);
        String role = user.getRole().name();

        if (role.equals("STAFF") || role.equals("ADMIN")) {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
            } else {
                throw new CustomException("Old password is incorrect", HttpStatus.UNAUTHORIZED);
            }
        }

        userService.changePassword(user);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/owner/change-password")
    @Operation(
            summary = "Change owner password",
            description = "Change owner's current password.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "owner's id", required = true),
                    @Parameter(name = "oldPassword", description = "Owner's old password", required = true),
                    @Parameter(name = "newPassword", description = "Owner's new password", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> changeOwnerPassword(@RequestParam("id") UUID id, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {

        User user = userService.findById(id);

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new CustomException("Old password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        userService.changePassword(user);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Login to the app.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "username", description = "User's username", required = true),
                    @Parameter(name = "password", description = "User's password", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> login(@ModelAttribute LoginRequest loginRequest) {
        objectValidator.validate(loginRequest);

        LoginResponse loginResponse = authService.login(loginRequest);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", loginResponse);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'STAFF')")
    @PostMapping("/logout")
    @Operation(
            summary = "Logout account",
            description = "Logout from app. Requires any role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "authorization header", description = "String authorization header", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", "logout success");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'STAFF')")
    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh access token",
            description = "Refresh access token to stay logged in. Requires any role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "Map String", description = "Map refresh_token and value", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> refreshAccessToken(@ModelAttribute Map<String, String> request) {
        Map<String, String> responseMap = authService.refreshToken(request);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", responseMap);

        return ResponseEntity.ok(response);
    }
}
