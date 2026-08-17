package com.enso.dto.response.auth;

import com.enso.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private Long userId;
    private String phone;
    private String token;
    private String tokenType;
    private Role role;
}