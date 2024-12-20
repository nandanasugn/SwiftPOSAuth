package com.swiftpos.swiftposauth.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthEvent implements Serializable {
    private String action;
    private String timestamp;
    private String userId;
    private String role;
    private String userName;
    private String userDetailId;
}
