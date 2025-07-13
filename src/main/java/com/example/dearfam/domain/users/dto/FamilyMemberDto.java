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
@Builder
public class FamilyMemberDto {
    private Long familyMemberId;
    private String familyMemberNickname;
    private UserFamilyRole familyMemberRole;
    private String familyMemberProfileImage;

}
