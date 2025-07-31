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
    private Long familyId;
    private String diaryImage;
    private LocalDateTime createdAt;

    public static DiaryBookDto from(DiaryBook diaryBook) {
        return DiaryBookDto.builder()
                .id(diaryBook.getId())
                .familyId(diaryBook.getFamily().getId())
                .diaryImage(diaryBook.getDiaryImage())
                .createdAt(diaryBook.getCreatedAt())
                .build();
    }
}
