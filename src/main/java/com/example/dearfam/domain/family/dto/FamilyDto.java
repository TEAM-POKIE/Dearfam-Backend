package com.example.dearfam.domain.family.dto;

import com.example.dearfam.domain.family.entity.Family;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class FamilyDto {
    private Long id;
    private String familyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FamilyDto from(Family family) {
        return FamilyDto.builder()
                .id(family.getId())
                .familyName(family.getFamilyName())
                .createdAt(family.getCreatedAt())
                .updatedAt(family.getUpdatedAt())
                .build();
    }

    public static List<FamilyDto> from(List<Family> families) {
        return families.stream()
                .map(FamilyDto::from)
                .collect(Collectors.toList());
    }
}
