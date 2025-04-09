package com.example.dearfam.domain.auth.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.domain.auth.controller.request.OAuth2LoginRequest;
import com.example.dearfam.domain.auth.controller.response.OAuth2LoginResponse;
import com.example.dearfam.domain.auth.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "OAuth2 Controller", description = "소셜 로그인을 처리하는 API")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @Operation(
            summary = "OAuth2 소셜 로그인",
            description = "인가 코드와 리디렉션 URI를 전달하여 사용자의 액세스 토큰과 리프레쉬 토큰 발급. [provider : kakao]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "잘못된 provider"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/oauth2/login")
    public Response<OAuth2LoginResponse> oAuth2Login(@Valid @RequestBody OAuth2LoginRequest request) {
        String provider = request.getProvider();
        String code = request.getCode();
        String redirectUri = request.getRedirectUri();

        OAuth2LoginResponse response = oAuth2Service.handleOAuth2Login(provider, code, redirectUri);

        return Response.data(response);
    }

}
