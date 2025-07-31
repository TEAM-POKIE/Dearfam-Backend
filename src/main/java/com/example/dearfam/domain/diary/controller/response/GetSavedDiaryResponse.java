package com.example.dearfam.domain.diary.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetSavedDiaryResponse {

    private String savedDiaryImageUrl;

    public static GetSavedDiaryResponse from(String savedDiaryImageUrl) {
        return GetSavedDiaryResponse.builder()
                .savedDiaryImageUrl(savedDiaryImageUrl)
                .build();
    }

}
