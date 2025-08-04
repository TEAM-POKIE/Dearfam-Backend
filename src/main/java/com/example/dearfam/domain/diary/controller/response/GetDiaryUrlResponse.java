package com.example.dearfam.domain.diary.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetDiaryUrlResponse {

    private final Long diaryBookId;
    private final String diaryImageUrl;

    public static GetDiaryUrlResponse from(Long diaryBookId, String diaryImageUrl) {
        return GetDiaryUrlResponse.builder()
                .diaryBookId(diaryBookId)
                .diaryImageUrl(diaryImageUrl)
                .build();
    }

}
