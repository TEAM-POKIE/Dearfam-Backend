package com.example.dearfam.domain.auth.controller.response;

import com.example.dearfam.domain.auth.dto.AuthTokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class OAuth2LoginResponse {
    private AuthTokenDto accessToken;
    private AuthTokenDto refreshToken;

    public static OAuth2LoginResponse from(AuthTokenDto accessToken, AuthTokenDto refreshToken) {
        return OAuth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
