package com.example.dearfam.domain.animatedphoto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiAnimatePhotoDto {

    @JsonProperty("video_url")
    private final String videoUrl;

    private final String status;

    private final String message;

}
