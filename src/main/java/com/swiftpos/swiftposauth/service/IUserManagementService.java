package com.swiftpos.swiftposauth.service;

import com.swiftpos.swiftposauth.dto.request.UserUpdateRequest;
import com.swiftpos.swiftposauth.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IUserManagementService {
    Page<UserResponse> searchUser(int page, int size, String keyword);

    void updateUser(UUID id, UserUpdateRequest request);

    void deleteUser(UUID id);
}
