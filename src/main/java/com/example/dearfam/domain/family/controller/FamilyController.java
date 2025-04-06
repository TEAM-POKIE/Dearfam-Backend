package com.example.dearfam.domain.family.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.family.controller.request.FamilyNameRequest;
import com.example.dearfam.domain.family.controller.request.UserFamilyRoleRequest;
import com.example.dearfam.domain.family.controller.response.GetFamilyResponse;
import com.example.dearfam.domain.family.controller.response.GetJoinedFamilyResponse;
import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.family.dto.InviteLinkResponse;
import com.example.dearfam.domain.family.service.FamilyService;
import com.example.dearfam.domain.family.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;
    private final JwtService jwtService;
    private final InviteService inviteService;

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

    @Operation(
            summary = "초대 링크 생성",
            description = "로그인한 유저의 가족의 초대 링크를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "유저 또는 가족 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/generate-link")
    public Response<InviteLinkResponse> generateInviteLink() {
        Long userId = jwtService.getTokenDto().getUserId();

        InviteLinkResponse linkInfo = inviteService.generateInviteLink(userId);

        return Response.data(linkInfo);
    }

    @Operation(
            summary = "가족 참여",
            description = "초대 링크로 가족에 참여하고, 참여한 가족을 보여줍니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 코드"),
                    @ApiResponse(responseCode = "404", description = "유저 또는 가족 정보 없음"),
                    @ApiResponse(responseCode = "409", description = "이미 가족 존재함"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/join")
    public Response<GetJoinedFamilyResponse> joinFamily(@RequestParam String code) {
        Long userId = jwtService.getTokenDto().getUserId();

        Long familyId = inviteService.validateInviteCodeAndReturnFamilyId(code); // 유효성 검증 & 예외 처리
        FamilyDto familyDto =  familyService.addUserToFamily(userId, familyId);
        GetJoinedFamilyResponse joinedFamily = GetJoinedFamilyResponse.from(familyDto);

        return Response.data(joinedFamily);
    }

    @Operation(
            summary = "가족 조회",
            description = "로그인한 유저의 가족을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND or FAMILY_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/members")
    public Response<GetFamilyResponse> getUserFamily() {
        Long userId = jwtService.getTokenDto().getUserId();
        GetFamilyResponse family = familyService.getUserFamily(userId);
        return Response.data(family);
    }

    @Operation(
            summary = "가족 ID를 통한 조회",
            description = "가족 ID를 통해 가족을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "FAMILY_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/members/{familyId}")
    public Response<GetFamilyResponse> getUserFamilyByFamilyId(@PathVariable Long familyId) {
        GetFamilyResponse family = familyService.getFamilyByFamilyId(familyId);
        return Response.data(family);
    }

}
