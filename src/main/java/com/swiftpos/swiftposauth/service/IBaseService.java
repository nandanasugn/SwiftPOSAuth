package com.swiftpos.swiftposauth.service;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IBaseService<T, DTO> {
    T create(DTO dto);

    T update(UUID id, DTO dto);

    void delete(UUID id);

    T findById(UUID id);

    Page<T> findAll(int page, int size, String keyword);
}
