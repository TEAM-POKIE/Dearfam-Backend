package com.example.dearfam.domain.auth.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "로그아웃, 회원탈퇴 기능 관리 컨트롤러")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @Operation(
            summary = "로그아웃",
            description = "로그아웃 시 서버에 저장된 리프레쉬 토큰을 무효화합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/logout")
    public Response<String> logout() {
        Long userId = jwtService.getTokenDto().getUserId();
        authService.logout(userId);

        return Response.data("로그아웃 성공");
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "서비스에서 완전히 탈퇴하고 모든 정보를 삭제합니다. 가족의 마지막 멤버일 경우 모든 가족 데이터가 삭제됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @DeleteMapping("/withdraw")
    public Response<String> withdraw() {
        Long userId = jwtService.getTokenDto().getUserId();
        authService.withdrawUser(userId);
        return Response.data("회원 탈퇴가 성공적으로 처리되었습니다.");
    }

}
