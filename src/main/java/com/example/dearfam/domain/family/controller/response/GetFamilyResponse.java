package com.example.dearfam.domain.family.controller.response;

import com.example.dearfam.domain.family.dto.FamilyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetFamilyResponse {
    private Long familyId;
    private String familyName;

    public static GetFamilyResponse from(FamilyDto familyDto) {
        return GetFamilyResponse.builder()
                .familyId(familyDto.getId())
                .familyName(familyDto.getFamilyName())
                .build();
    }
}
