package com.example.dearfam.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DiaryContentDto {
    // @JsonProperty 를 사용해서, python 의 결과 키 값과 매칭
    @JsonProperty("title")
    private final String title;

    @JsonProperty("content")
    private final String content;

    @JsonProperty("image_url")
    private final String imageUrl;

}
