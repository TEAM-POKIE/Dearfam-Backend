package com.example.dearfam.common.jwt.filter;

import com.example.dearfam.common.dto.token.TokenDto;
import com.example.dearfam.common.jwt.auth.JwtVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtVerifier jwtVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        TokenDto verifiedTokenDto = jwtVerifier.verify(request);
        UsernamePasswordAuthenticationToken authenticationToken = null;
        if (verifiedTokenDto != null) {
            authenticationToken = new UsernamePasswordAuthenticationToken(
                            verifiedTokenDto,
                            verifiedTokenDto.getUserId(),
                            Collections.singletonList(new SimpleGrantedAuthority(verifiedTokenDto.getUserRole()))
                    );
        }
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
