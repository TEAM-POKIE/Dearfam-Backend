package com.example.dearfam.common.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenDto {
    // AccessToken이나 RefreshToken을 전달할 때 사용하는 DTO
    private String token;
    private Long expiresIn;
}
