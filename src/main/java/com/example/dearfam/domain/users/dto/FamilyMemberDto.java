package com.example.dearfam.domain.users.dto;

import com.example.dearfam.domain.family.dto.FamilyDto;
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
    private Long familyMemberId;
    private String familyMemberNickname;
    private UserFamilyRole familyMemberRole;
    // TODO : 여기에 이미지도 같이 넣지~

    public static FamilyMemberDto from(Users familyMember) {
        return FamilyMemberDto.builder()
                .familyMemberId(familyMember.getId())
                .familyMemberNickname(familyMember.getUserNickname())
                .familyMemberRole(familyMember.getUserFamilyRole())
                .build();
    }

    public static List<FamilyMemberDto> from(List<Users> familyMembers) {
        return familyMembers.stream()
                .map(FamilyMemberDto::from)
                .collect(Collectors.toList());
    }
}
