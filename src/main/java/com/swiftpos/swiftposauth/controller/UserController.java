package com.swiftpos.swiftposauth.controller;

import com.swiftpos.swiftposauth.dto.request.UserUpdateRequest;
import com.swiftpos.swiftposauth.dto.response.CommonResponse;
import com.swiftpos.swiftposauth.dto.response.UserResponse;
import com.swiftpos.swiftposauth.exception.CustomException;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.service.IFileStorageService;
import com.swiftpos.swiftposauth.service.IUserManagementService;
import com.swiftpos.swiftposauth.service.IUserService;
import com.swiftpos.swiftposauth.util.MapperUtil;
import com.swiftpos.swiftposauth.util.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {
    private final IUserManagementService userManagementService;
    private final IFileStorageService fileStorageService;
    private final ObjectValidator objectValidator;
    private final IUserService userService;

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @GetMapping("users/search")
    @Operation(
            summary = "Search users",
            description = "Search users record. Requires OWNER or ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "page", description = "Page of data"),
                    @Parameter(name = "size", description = "Size of data"),
                    @Parameter(name = "keyword", description = "User keyword", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> searchUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword) {
        Page<UserResponse> userResponses = userManagementService.searchUser(page, size, keyword);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", userResponses);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @PatchMapping("/user")
    @Operation(
            summary = "Update admin or staff details",
            description = "Update user details record by their id. Requires OWNER or ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "Admin or staff id", required = true),
                    @Parameter(name = "request", description = "User update request", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> updateUser(@RequestParam("id") UUID id, @ModelAttribute UserUpdateRequest request) {
        objectValidator.validate(request);

        User user = userService.findById(id);

        String role = user.getRole().name();
        if (role.equals("ADMIN") || role.equals("STAFF")) {
            userManagementService.updateUser(id, request);
        }

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER')")
    @PutMapping("'/user/owner")
    @Operation(
            summary = "Update admin or staff details",
            description = "Update user details record by their id. Requires OWNER or ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "Admin or staff id", required = true),
                    @Parameter(name = "request", description = "User update request", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> updateOwner(@RequestParam("id") UUID id, @ModelAttribute UserUpdateRequest request) {
        objectValidator.validate(request);

        userManagementService.updateUser(id, request);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @DeleteMapping("/user")
    @Operation(
            summary = "Delete an employee",
            description = "Delete a staff's record by their id. Requires OWNER or ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "Staff's id", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> deleteEmployee(@RequestParam("id") UUID id) {
        User user = userService.findById(id);

        String role = user.getRole().name();
        if (role.equals("STAFF")) {
            userManagementService.deleteUser(id);
        } else {
            throw new CustomException("Can't delete this user", HttpStatus.FORBIDDEN);
        }

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/user/owner")
    @Operation(
            summary = "Delete admin or staff",
            description = "Delete an admin or staff record by their id. Requires OWNER role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "id", description = "Admin or Staff id", required = true)
            },
            tags = {"User Management"}
    )
    public ResponseEntity<?> deleteUser(@RequestParam("id") UUID id) {
        userManagementService.deleteUser(id);

        CommonResponse<?> response = MapperUtil.mapToCommonResponse(HttpStatus.OK.value(), "success", null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'STAFF', 'ADMIN')")
    @GetMapping("user/image/{subfolder}/{fileName}")
    @Operation(
            summary = "Fetch file",
            description = "Retrieves a file from the specified subfolder by file name. Accessible to users with any roles.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "subfolder", description = "Subfolder name where the file is stored", required = true),
                    @Parameter(name = "fileName", description = "Name of the file to fetch", required = true)
            },
            tags = {"File Controller"}
    )
    public ResponseEntity<?> getImage(@PathVariable String subfolder, @PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.getFile(fileName, subfolder);
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));
            contentType = (contentType != null) ? contentType : "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
