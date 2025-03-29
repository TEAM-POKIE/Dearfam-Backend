package com.example.dearfam.domain.users.dto;

import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class UsersDto {
    private Long id;
    private String userNickName;
    private String userRole;
    private String userFamilyRole;
    private Boolean isFamilyRoomManager;
    private String profileImage;

    public static UsersDto from(Users user) {
        return UsersDto.builder()
                .id(user.getId())
                .userNickName(user.getUserNickname())
                .userRole(user.getUserRole())
                .userFamilyRole(user.getUserFamilyRole())
                .isFamilyRoomManager(user.getIsFamilyRoomManager())
                .profileImage(user.getProfileImage())
                .build();
    }

    public static List<UsersDto> from(List<Users> users) {
        return users.stream()
                .map(UsersDto::from)
                .collect(Collectors.toList());
    }
}
