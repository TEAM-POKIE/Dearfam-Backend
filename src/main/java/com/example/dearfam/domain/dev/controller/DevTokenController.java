package com.example.dearfam.domain.dev.controller;

import com.example.dearfam.domain.auth.dto.AuthTokenDto;
import com.example.dearfam.common.jwt.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/dev")
@Tag(name = "🖥️ 개발용 토큰 API", description = "개발 환경에서만 사용할 수 있는 토큰 발급 API")
@RequiredArgsConstructor
public class DevTokenController {

    private final JwtService jwtService;

    @GetMapping("/token")
    @Operation(summary = "개발용 리프레시 토큰 발급", description = "개발 환경에서 테스트용 리프레시 토큰을 발급합니다.")
    public AuthTokenDto issueRefreshToken(
            @Parameter(description = "사용자 ID") @RequestParam(defaultValue = "1") Long userId,
            @Parameter(description = "사용자 역할 (ROLE_USER, ROLE_ADMIN 등)") @RequestParam(defaultValue = "ROLE_DEV") String userRole) {

        return jwtService.createRefreshToken(userId, userRole);
    }
}