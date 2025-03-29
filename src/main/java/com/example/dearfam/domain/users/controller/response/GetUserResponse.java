package com.example.dearfam.domain.users.controller.response;

import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetUserResponse {
    private Long id;
    private String userNickName;
    private String userRole;
    private String userFamilyRole;
    private Boolean isFamilyRoomManager;
    private String profileImage;

    public static GetUserResponse from(UsersDto usersDto) {
        return GetUserResponse.builder()
                .id(usersDto.getId())
                .userNickName(usersDto.getUserNickName())
                .userRole(usersDto.getUserRole())
                .userFamilyRole(usersDto.getUserFamilyRole())
                .isFamilyRoomManager(usersDto.getIsFamilyRoomManager())
                .profileImage(usersDto.getProfileImage())
                .build();
    }

}
