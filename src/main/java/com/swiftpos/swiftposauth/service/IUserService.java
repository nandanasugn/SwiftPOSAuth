package com.swiftpos.swiftposauth.service;

import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends IBaseService<User, UserRequest>, UserDetailsService {
    User getUserByUsername(String username);

    User getOwnerByUsername(String username);

    void changePassword(User user);
}
