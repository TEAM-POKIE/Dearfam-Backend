package com.example.dearfam.domain.diary.dto;

import com.example.dearfam.domain.diary.entity.DiaryBook;
import com.example.dearfam.domain.diary.entity.DiaryCoverColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DiaryBookDto {
    private Long id;
    private String diaryTitle;
    private DiaryCoverColor diaryCoverColor;
    private LocalDateTime createdAt;

    public static DiaryBookDto from(DiaryBook diaryBook) {
        return DiaryBookDto.builder()
                .id(diaryBook.getId())
                .diaryTitle(diaryBook.getDiaryTitle())
                .diaryCoverColor(diaryBook.getDiaryCoverColor())
                .createdAt(diaryBook.getCreatedAt())
                .build();
    }
}
