package com.example.dearfam.common.jwt.filter;

import com.example.dearfam.common.dto.token.TokenDto;
import com.example.dearfam.common.jwt.auth.JwtVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtVerifier jwtVerifier;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    // 유효한 토큰이 반환될 경우 (TokenDto 설정)
    // SecurityContext에 인증정보가 있어야함.
    @Test
    void doFilterInternal_ValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // given
        TokenDto tokenDto = new TokenDto(1L, "USER");
        when(jwtVerifier.verify(request)).thenReturn(tokenDto);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        // 인증정보가 Null값이 아닌지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        // 인증정보에 저장된 토큰에서 userId가 1L인지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(TokenDto.class)
                .extracting("userId").isEqualTo(1L);
        // 인증정보에 저장된 토큰에서 userRole이 ROLE_USER인지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(TokenDto.class)
                .extracting("userRole").isEqualTo("USER");
    }

    // 유효하지 않은 토큰을 리턴할 경우 (Null로 설정)
    // SecurityContext에 인증정보가 설정되지 않아야 함.
    @Test
    void doFilterInternal_InvalidToken_ShouldThrowServletException() throws ServletException, IOException {
        when(jwtVerifier.verify(request)).thenReturn(null);
        jwtFilter.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}