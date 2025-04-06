package com.example.dearfam.domain.users.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.users.controller.request.UserNicknameRequest;
import com.example.dearfam.domain.users.controller.response.GetUserResponse;
import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
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

}
