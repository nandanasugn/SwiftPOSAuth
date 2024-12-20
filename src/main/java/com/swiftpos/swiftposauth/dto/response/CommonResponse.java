package com.swiftpos.swiftposauth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
public class CommonResponse<T> {
    @Schema(description = "HTTP status code of the response", example = "200")
    private int status;

    @Schema(description = "Message describing the result of the operation", example = "Success")
    private String message;

    @Schema(description = "Timestamp of the response")
    private LocalDateTime timestamp;

    @Schema(description = "The payload of the response. Can be any type of data", nullable = true)
    private T data;
}
