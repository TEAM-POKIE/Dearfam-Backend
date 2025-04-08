package com.example.dearfam.common.jwt.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.dearfam.domain.auth.dto.AuthTokenDto;
import com.example.dearfam.common.dto.token.TokenDto;
import com.example.dearfam.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtService {
    private final Algorithm tokenAlgorithm;
    @Value("${app.jwt.accessTokenValidMS}") private Long accessTokenValidMilliseconds;
    @Value("${app.jwt.refreshTokenValidMS}") private Long refreshTokenValidMilliseconds;

    // 토큰 생성
    private AuthTokenDto createToken(Long userId, String userRole, Long tokenValidMilliseconds) {
        Date expiresAt = new Date(System.currentTimeMillis() + tokenValidMilliseconds);

        String token = JWT.create()
                .withClaim("userId", userId)
                .withClaim("userRole", userRole)
                .withExpiresAt(expiresAt)
                .sign(tokenAlgorithm);

        return new AuthTokenDto(token, tokenValidMilliseconds);
    }

    // access token 생성
    public AuthTokenDto createAccessToken(Long userId, String userRole) {
        return createToken(userId, userRole, accessTokenValidMilliseconds);
    }

    // refresh token 생성
    public AuthTokenDto createRefreshToken(Long userId, String userRole) {
        return createToken(userId, userRole, refreshTokenValidMilliseconds);
    }

    // TokenDto 값들에 접근하기 위한 함수
    public TokenDto getTokenDto() {
        TokenDto tokenDto = (TokenDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (tokenDto == null) {
            throw new CustomException("TokenDto is null");
        }
        return tokenDto;
    }
}
