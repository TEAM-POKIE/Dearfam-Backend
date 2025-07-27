package com.example.dearfam.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DiaryContentDto {

    private final Integer pageNumber;

    private final String title;

    private final String text;

    private final String imageUrl;

}
