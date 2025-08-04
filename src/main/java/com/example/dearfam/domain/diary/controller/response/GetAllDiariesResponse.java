package com.example.dearfam.domain.diary.controller.response;

import com.example.dearfam.domain.diary.entity.DiaryBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAllDiariesResponse {

    private final Long diaryBookId;
    private final String diaryImageUrl;

    public static GetAllDiariesResponse from(Long diaryBookId, String diaryImageUrl) {
        return GetAllDiariesResponse.builder()
                .diaryBookId(diaryBookId)
                .diaryImageUrl(diaryImageUrl)
                .build();
    }

}
