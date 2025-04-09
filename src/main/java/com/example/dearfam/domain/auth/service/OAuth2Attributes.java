package com.example.dearfam.domain.auth.service;

import com.example.dearfam.domain.auth.exception.AuthErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes {
    // 소셜에서 가져온 정보를 저장하는 클래스
    // 필요한 정보 : 유저 닉네임, 이미지, 이메일(식별자), 소셜 제공자 ID
    private final String userNickname;
    private final String email;
    private final String profileImageUrl;
    private final String socialProvider;
    private final String socialUserId;

    public static OAuth2Attributes of(Map<String, Object> attributes, String provider, String socialUserId) {
        switch (provider) {
            case "kakao":
                return ofKakao(attributes, provider, socialUserId);
            default:
                throw AuthErrorCode.NOT_SUPPORTED_SOCIAL_PROVIDER.defaultException();
        }
    }

    public static OAuth2Attributes ofKakao(Map<String, Object> attributes, String provider, String socialUserId) {
        // 여기서 이제 KakaoService 구현하고 오기!
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            throw AuthErrorCode.GET_USER_INFO_FAILED_FROM_SOCIAL_PROVIDER.defaultException();
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            throw AuthErrorCode.KAKAO_PROFILE_IS_NULL.defaultException();
        }

        // 이메일 추출
        String email = (String) kakaoAccount.get("email");
        if (email == null) {
            throw AuthErrorCode.KAKAO_EMAIL_IS_NULL.defaultException();
        }

        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("thumbnail_image_url");
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            profileImageUrl = null;
        }

        return OAuth2Attributes.builder()
                .userNickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .socialProvider(provider)
                .socialUserId(socialUserId)
                .build();
    }

}
