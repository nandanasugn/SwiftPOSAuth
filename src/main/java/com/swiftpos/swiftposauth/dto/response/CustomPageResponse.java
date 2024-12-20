package com.swiftpos.swiftposauth.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class CustomPageResponse<T> {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
    private boolean isFirstPage;
    private boolean isLastPage;
}
