package com.example.dearfam.domain.users.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.users.controller.response.GetUpdatedProfileImageResponse;
import com.example.dearfam.domain.users.controller.request.UserNicknameRequest;
import com.example.dearfam.domain.users.controller.response.GetUserResponse;
import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users Controller", description = "사용자 관련 기능(조회, 수정)을 처리하는 API")
public class UsersController {
    private final UsersService usersService;
    private final JwtService jwtService;

    @Operation(
            summary = "로그인 유저 정보 조회",
            description = "현재 로그인한 유저의 정보를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/user")
    public Response<GetUserResponse> getUser() {
        Long userId = jwtService.getTokenDto().getUserId();
        UsersDto usersDto = usersService.getUserDtoById(userId);

        return Response.data(GetUserResponse.from(usersDto));
    }

    @Operation(
            summary = "id로 유저 정보 조회",
            description = "userId로 유저의 정보를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/{userId}")
    public Response<GetUserResponse> getUserByUserId(@NotNull @PathVariable Long userId) {
        UsersDto usersDto = usersService.getUserDtoById(userId);
        return Response.data(GetUserResponse.from(usersDto));
    }

    @Operation(
            summary = "닉네임(이름) 변경",
            description = "현재 로그인한 유저의 닉네임을 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "409", description = "EXISTING_NICKNAME"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PutMapping("/nickname")
    public Response<String> updateUserNickname(@Valid @RequestBody UserNicknameRequest userNicknameRequest) {
        Long userId = jwtService.getTokenDto().getUserId();
        usersService.updateUserNickname(userId, userNicknameRequest.getNickname());
        return Response.data("User Nickname Updated");
    }

    @Operation(
        summary = "프로필 사진 변경",
        description = "현재 로그인한 유저의 프로필 이미지를 업데이트 합니다.",
        responses = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
        }
    )
    @PutMapping(value = "/profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<GetUpdatedProfileImageResponse> updateUserProfileImage(
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        log.info(profileImage.getContentType());
        Long userId = jwtService.getTokenDto().getUserId();

        // 프로필 사진 업데이트
        String profileImageUrl = usersService.updateUserProfileImage(userId, profileImage);

        return Response.data("이미지 업데이트를 완료했습니다", GetUpdatedProfileImageResponse.from(profileImageUrl));
    }
}
