package com.example.dearfam.domain.animatedphoto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAnimatePhotoTempUrlResponse {

    private final String animatePhotoTempUrl;

    public static GetAnimatePhotoTempUrlResponse from(String animatePhotoTempUrl) {
        return GetAnimatePhotoTempUrlResponse.builder()
                .animatePhotoTempUrl(animatePhotoTempUrl)
                .build();
    }

}
