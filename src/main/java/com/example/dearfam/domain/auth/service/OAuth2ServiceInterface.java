package com.example.dearfam.domain.auth.service;

public interface OAuth2ServiceInterface {
    String getAccessToken(String code, String redirectUri);
    OAuth2Attributes getOAuth2UserInfo(String accessToken, String provider);
}
