package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.exception.CustomException;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.model.enums.ERole;
import com.swiftpos.swiftposauth.repository.IUserRepository;
import com.swiftpos.swiftposauth.service.IUserService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final ObjectValidator objectValidator;
    private final IUserRepository userRepository;

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Override
    public User getOwnerByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void changePassword(User user) {
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }

        userRepository.save(user);
    }

    @Override
    public User create(UserRequest userRequest) {
        objectValidator.validate(userRequest);

        try {
            User user = MapperUtil.mapToUser(userRequest);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException("User creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User update(UUID id, UserRequest userRequest) {
        objectValidator.validate(userRequest);

        try {
            User user = findById(id);
            if (user.getUsername() == null) {
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }

            UpdateUtil.updateIfNotNull(userRequest.getUsername(), user::setUsername);
            UpdateUtil.updateIfNotNull(userRequest.getPassword(), user::setPassword);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException("User update failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Page<User> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));

        Page<User> users;
        if (keyword == null) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findUsersByUsernameContainingIgnoreCase(pageable, keyword);
        }

        if (users.isEmpty()) {
            throw new CustomException("No users found", HttpStatus.NOT_FOUND);
        }

        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }
}
