package com.example.dearfam.domain.users.dto;

import com.example.dearfam.domain.users.entity.UserFamilyRole;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class FamilyMemberDto {
    private Long userId;
    private String userNickname;
    private UserFamilyRole userFamilyRole;

    public static FamilyMemberDto from(Long userId, String userNickname, UserFamilyRole userFamilyRole) {
        return FamilyMemberDto.builder()
                .userId(userId)
                .userNickname(userNickname)
                .userFamilyRole(userFamilyRole)
                .build();
    }

    public static List<FamilyMemberDto> from(List<Users> users) {
        return users.stream()
                .map(user -> FamilyMemberDto.from(
                        user.getId(),
                        user.getUserNickname(),
                        user.getUserFamilyRole()
                ))
                .collect(Collectors.toList());
    }
}
