package com.example.dearfam.domain.family.controller.response;

import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.users.dto.FamilyMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetFamilyResponse {
    private Long familyId;
    private String familyName;
    private List<FamilyMemberDto> familyMembers;

    public static GetFamilyResponse from(FamilyDto familyDto, List<FamilyMemberDto> familyMembers) {
        return GetFamilyResponse.builder()
                .familyId(familyDto.getId())
                .familyName(familyDto.getFamilyName())
                .familyMembers(familyMembers)
                .build();
    }
}
