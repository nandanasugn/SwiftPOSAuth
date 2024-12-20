package com.swiftpos.swiftposauth.repository;

import com.swiftpos.swiftposauth.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserDetailRepository extends JpaRepository<UserDetail, UUID> {
    UserDetail findByFullName(String fullName);

    Page<UserDetail> findUserDetailsByFullNameContainingIgnoreCase(Pageable pageable, String fullName);
}
