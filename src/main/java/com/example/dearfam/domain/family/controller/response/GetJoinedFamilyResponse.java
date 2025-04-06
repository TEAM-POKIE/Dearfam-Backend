package com.example.dearfam.domain.family.controller.response;

import com.example.dearfam.domain.family.dto.FamilyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetJoinedFamilyResponse {
    private Long joinedFamilyId;
    private String joinedFamilyName;

    public static GetJoinedFamilyResponse from(FamilyDto familyDto) {
        return GetJoinedFamilyResponse.builder()
                .joinedFamilyId(familyDto.getId())
                .joinedFamilyName(familyDto.getFamilyName())
                .build();
    }
}
