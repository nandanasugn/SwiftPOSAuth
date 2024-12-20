package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.dto.request.UserDetailRequest;
import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.dto.request.UserUpdateRequest;
import com.swiftpos.swiftposauth.dto.response.UserResponse;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.model.UserDetail;
import com.swiftpos.swiftposauth.service.IUserDetailService;
import com.swiftpos.swiftposauth.service.IUserManagementService;
import com.swiftpos.swiftposauth.service.IUserService;
import com.swiftpos.swiftposauth.util.ObjectValidator;
import com.swiftpos.swiftposauth.util.UpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementServiceImpl implements IUserManagementService {
    private final ObjectValidator objectValidator;
    private final IUserService userService;
    private final IUserDetailService userDetailService;

    @Override
    public Page<UserResponse> searchUser(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userService.findAll(page, size, keyword);
        Page<UserDetail> userDetails = userDetailService.findAll(page, size, keyword);

        List<UserResponse> userResponses = users.stream()
                .map(user -> {
                    UserDetail userDetail = userDetails.getContent()
                            .stream()
                            .filter(detail -> detail.getUser().getId().equals(user.getId()))
                            .findFirst()
                            .orElse(null);

                    assert userDetail != null;
                    return UserResponse.builder()
                            .userId(user.getId())
                            .userDetailId(userDetail.getId())
                            .username(user.getUsername())
                            .role(user.getRole().name())
                            .fullName(userDetail.getFullName())
                            .email(userDetail.getEmail())
                            .address(userDetail.getAddress())
                            .phone(userDetail.getPhone())
                            .birthday(userDetail.getDateOfBirth())
                            .imageUrl(userDetail.getImageUrl())
                            .build();
                }).collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    @Override
    public void updateUser(UUID id, UserUpdateRequest request) {
        objectValidator.validate(request);

        User user = userService.findById(id);

        UserRequest userRequest = UserRequest.builder()
                .username(request.getUsername())
                .role(user.getRole().name())
                .build();
        user = userService.update(id, userRequest);

        UserDetailRequest userDetailRequest = UserDetailRequest.builder()
                .userId(id)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phone(request.getPhone())
                .birthday(request.getBirthday())
                .file(request.getFile())
                .build();
        userDetailService.update(user.getUserDetail().getId(), userDetailRequest);
    }

    @Override
    public void deleteUser(UUID id) {
        objectValidator.validate(id);
        User user = userService.findById(id);
        UUID userDetailId = user.getUserDetail().getId();
        userService.delete(userDetailId);
        userDetailService.delete(userDetailId);
    }
}
