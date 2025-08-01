package com.example.dearfam.domain.animatedphoto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAnimatePhotoResponse {

    private final String animatePhotoUrl;

    public static GetAnimatePhotoResponse from(String animatePhotoUrl) {
        return GetAnimatePhotoResponse.builder()
                .animatePhotoUrl(animatePhotoUrl)
                .build();
    }

}
