package com.example.dearfam.domain.diary.dto;

import com.example.dearfam.domain.diary.entity.DiaryPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DiaryPageDto {

    private Long id;
    private Long diaryBookId;
    private Integer diaryPageNumber;
    private String diaryPageUrl;

    public static DiaryPageDto from(DiaryPage diaryPage) {
        return DiaryPageDto.builder()
                .id(diaryPage.getId())
                .diaryBookId(diaryPage.getDiaryBook().getId())
                .diaryPageNumber(diaryPage.getDiaryPageNumber())
                .diaryPageUrl(diaryPage.getDiaryPageUrl())
                .build();
    }

}
