package com.example.dearfam.domain.diary.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetSavedDiaryResponse {

    private Long diaryId;
    private String savedDiaryImageUrl;

    public static GetSavedDiaryResponse from(Long diaryId, String savedDiaryImageUrl) {
        return GetSavedDiaryResponse.builder()
                .diaryId(diaryId)
                .savedDiaryImageUrl(savedDiaryImageUrl)
                .build();
    }

}
