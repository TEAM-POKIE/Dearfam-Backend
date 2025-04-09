package com.example.dearfam.domain.auth.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2LoginRequest {

    @NotEmpty
    private String provider; // KAKAO, 추후 GOOGLE, APPLE 들어올 수 있음.

    @NotEmpty
    private String code; //인가 코드

    @NotEmpty
    private String redirectUri;
}
