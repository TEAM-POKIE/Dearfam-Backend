package com.example.dearfam.common.jwt.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.example.dearfam.domain.auth.dto.AuthTokenDto;
import com.example.dearfam.common.dto.token.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        Algorithm tokenAlgorithm = Algorithm.HMAC256("test-secret-key"); // 실제 알고리즘 객체 생성
        jwtService = new JwtService(tokenAlgorithm);

        ReflectionTestUtils.setField(jwtService, "accessTokenValidMilliseconds", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenValidMilliseconds", 86400000L);
    }

    @Test
    void createAccessToken_ShouldReturnValidToken() {
        // given
        Long userId = 1L;
        String userRole = "ROLE_USER";

        // when
        AuthTokenDto result = jwtService.createAccessToken(userId, userRole);

        // then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getToken().startsWith("eyJ")); // JWT 토큰은 보통 "eyJ"로 시작
    }

    @Test
    void createRefreshToken_ShouldReturnValidToken() {
        // given
        Long userId = 1L;
        String userRole = "ROLE_USER";

        // when
        AuthTokenDto result = jwtService.createRefreshToken(userId, userRole);

        // then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getToken().startsWith("eyJ"));
    }

    @Test
    void getTokenDto_ShouldReturnCurrentUserToken() {
        // given
        TokenDto mockTokenDto = new TokenDto(1L, "ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockTokenDto, null);

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        TokenDto result = jwtService.getTokenDto();

        // then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("ROLE_USER", result.getUserRole());
    }
}