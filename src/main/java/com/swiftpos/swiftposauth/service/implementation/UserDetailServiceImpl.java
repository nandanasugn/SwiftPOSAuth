package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.dto.request.UserDetailRequest;
import com.swiftpos.swiftposauth.exception.CustomException;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.model.UserDetail;
import com.swiftpos.swiftposauth.repository.IUserDetailRepository;
import com.swiftpos.swiftposauth.service.*;
import com.swiftpos.swiftposauth.util.MapperUtil;
import com.swiftpos.swiftposauth.util.ObjectValidator;
import com.swiftpos.swiftposauth.util.UpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceImpl implements IUserDetailService {
    private final ObjectValidator objectValidator;
    private final IUserDetailRepository userDetailRepository;
    private final IUserService userService;
    private final IFileStorageService fileStorageService;

    @Override
    public UserDetail create(UserDetailRequest userDetailRequest) {
        objectValidator.validate(userDetailRequest);

        try {
            User user = userService.findById(userDetailRequest.getUserId());
            if (user == null) {
                throw new CustomException("User detail not found", HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = MapperUtil.mapToUserDetail(userDetailRequest);
            userDetail.setImageUrl(fileStorageService.storeFile(userDetailRequest.getFile(), "images"));
            userDetail.setUser(user);

            user.setUserDetail(userDetail);

            return userDetailRepository.save(userDetail);
        } catch (Exception e) {
            throw new CustomException("Failed to create user detail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetail update(UUID id, UserDetailRequest userDetailRequest) {
        objectValidator.validate(userDetailRequest);

        try {
            UserDetail userDetail = findById(id);
            if (userDetail == null) {
                throw new CustomException("User detail not found", HttpStatus.NOT_FOUND);
            }

            UpdateUtil.updateIfNotNull(userDetailRequest.getFullName(), userDetail::setFullName);
            UpdateUtil.updateIfNotNull(userDetailRequest.getEmail(), userDetail::setEmail);
            UpdateUtil.updateIfNotNull(userDetailRequest.getAddress(), userDetail::setAddress);
            UpdateUtil.updateIfNotNull(userDetailRequest.getPhone(), userDetail::setPhone);
            UpdateUtil.updateIfNotNull(userDetailRequest.getBirthday(), userDetail::setDateOfBirth);

            if (userDetailRequest.getBirthday() != null) {
                fileStorageService.deleteFile(userDetail.getImageUrl());
                String imageUrl = fileStorageService.storeFile(userDetailRequest.getFile(), "images");
                UpdateUtil.updateIfNotNull(imageUrl, userDetail::setImageUrl);
            }

            return userDetailRepository.save(userDetail);
        } catch (Exception e) {
            throw new CustomException("Failed to update user detail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(UUID id) {
        UserDetail userDetail = findById(id);
        if (userDetail == null) {
            throw new CustomException("User detail not found", HttpStatus.NOT_FOUND);
        }

        userDetail.setDeleted(true);
        userDetailRepository.save(userDetail);
    }

    @Override
    public UserDetail findById(UUID id) {
        return userDetailRepository.findById(id).orElseThrow(() -> new CustomException("User detail not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Page<UserDetail> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fullName"));

        Page<UserDetail> userDetails;
        if (keyword == null) {
            userDetails = userDetailRepository.findAll(pageable);
        } else {
            userDetails = userDetailRepository.findUserDetailsByFullNameContainingIgnoreCase(pageable, keyword);
        }

        if (userDetails.isEmpty()) {
            throw new CustomException("No user details found", HttpStatus.NOT_FOUND);
        }

        return userDetails;
    }
}
