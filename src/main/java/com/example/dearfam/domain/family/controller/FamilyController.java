package com.example.dearfam.domain.family.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.family.controller.request.FamilyNameRequest;
import com.example.dearfam.domain.family.controller.request.UserFamilyRoleRequest;
import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.family.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;
    private final JwtService jwtService;

    @Operation(
            summary = "가족 생성",
            description = "현재 로그인한 유저가 새롭게 가족을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "409", description = "가족 이름 중복"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping
    public Response<FamilyDto> createFamily(@Valid @RequestBody FamilyNameRequest request) {
        Long userId = jwtService.getTokenDto().getUserId();
        FamilyDto familyDto = familyService.createFamily(request.getFamilyName(), userId);
        return Response.data(familyDto);
    }

    @Operation(
            summary = "가족 내 역할 설정",
            description = "클라이언트가 요청한 가족 역할을 유저 정보에 저장합니다. 값 : [FATHER, MOTHER, SON, DAUGHTER]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (Enum 파싱 실패 등)"),
                    @ApiResponse(responseCode = "404", description = "유저 또는 가족 정보 없음"),
                    @ApiResponse(responseCode = "409", description = "역할 중복 또는 인원 제한 초과"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/role")
    public Response<String> assignFamilyRole(@Valid @RequestBody UserFamilyRoleRequest request) {
        Long userId = jwtService.getTokenDto().getUserId();

        familyService.assignUserFamilyRole(userId, request.getFamilyRole());

        return Response.data(request.getFamilyRole() + " 역할을 설정했습니다.");
    }
}
