package com.example.dearfam.common.jwt.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.dearfam.common.dto.token.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtVerifierTest {

    private JwtVerifier jwtVerifier;
    private Algorithm tokenAlgorithm;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        tokenAlgorithm = Algorithm.HMAC256("test-secret-key");
        jwtVerifier = new JwtVerifier(tokenAlgorithm);
        request = mock(HttpServletRequest.class);
    }

    @Test
    void verify_ValidToken_ShouldReturnTokenDto() {
        // given
        String token = JWT.create()
                .withClaim("userId", 1L)
                .withClaim("userRole", "ROLE_USER")
                .sign(tokenAlgorithm);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when
        TokenDto tokenDto = jwtVerifier.verify(request);

        // then
        assertNotNull(tokenDto);
        assertEquals(1L, tokenDto.getUserId());
        assertEquals("ROLE_USER", tokenDto.getUserRole());
    }

    @Test
    void verify_InvalidToken_ShouldThrowException() {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        // when & then
        assertThrows(Exception.class, () -> jwtVerifier.verify(request));
    }
}