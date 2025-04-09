package com.example.dearfam.domain.auth.service;

import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.auth.controller.response.OAuth2LoginResponse;
import com.example.dearfam.domain.auth.dto.AuthTokenDto;
import com.example.dearfam.domain.auth.exception.AuthErrorCode;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.repository.UsersRepository;
import com.example.dearfam.domain.users.service.UsersPersistenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UsersRepository usersRepository;

    private final KakaoService kakaoService;
    private final JwtService jwtService;
    private final UsersPersistenceService usersPersistenceService;

    @Transactional
    public OAuth2LoginResponse handleOAuth2Login(String provider, String code, String redirectUri) {
        OAuth2Attributes oAuth2Attributes;
        String accessToken;
        switch (provider) {
            case "kakao":
                accessToken = kakaoService.getAccessToken(code, redirectUri);
                oAuth2Attributes = kakaoService.getOAuth2UserInfo(accessToken, provider);
                break;
            default:
                throw AuthErrorCode.NOT_SUPPORTED_SOCIAL_PROVIDER.defaultException();
        }

        return makeOAuth2LoginResponse(oAuth2Attributes);
    }

    private OAuth2LoginResponse makeOAuth2LoginResponse(OAuth2Attributes oAuth2Attributes) {
        // oAuth2Attributes 를 통해 정보를 모두 추출(닉네임, 이메일, 프로필 이미지) 그리고 provider
        // 그리고 JwtService 를 통해 accessToken, refreshToken 을 생성
        // UsersPersistenceService 에서 createUser 를 통해 User 를 생성 -> userRepository 에 유저 save
        // OAuth2LoginResponse 형식으로 반환
        String nickname = oAuth2Attributes.getUserNickname();
        String provider = oAuth2Attributes.getSocialProvider();
        String socialUserId = oAuth2Attributes.getSocialUserId();
        String profileImageUrl = oAuth2Attributes.getProfileImageUrl();

        String email = oAuth2Attributes.getEmail();
        Optional<Users> userOptional = usersRepository.findByEmail(email);

        // 유저가 존재하면 그냥 사용. 아니면 createUser
        Users user = userOptional.orElseGet(() -> usersPersistenceService.createUser(
                nickname, "USER", email,
                provider, socialUserId, profileImageUrl
        ));

        AuthTokenDto accessTokenDto = jwtService.createAccessToken(user.getId(), user.getUserRole());
        AuthTokenDto refreshTokenDto = jwtService.createRefreshToken(user.getId(), user.getUserRole());

        user.setRefreshToken(refreshTokenDto.getToken());
        usersRepository.save(user);

        return OAuth2LoginResponse.from(accessTokenDto, refreshTokenDto);
    }

}
