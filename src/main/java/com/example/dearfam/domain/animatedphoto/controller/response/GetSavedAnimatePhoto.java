package com.example.dearfam.domain.animatedphoto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetSavedAnimatePhoto {

    private final Long animatePhotoId;
    private final String animatePhotoUrl;

    public static GetSavedAnimatePhoto from(Long animatePhotoId, String animatePhotoUrl) {
        return GetSavedAnimatePhoto.builder()
                .animatePhotoId(animatePhotoId)
                .animatePhotoUrl(animatePhotoUrl)
                .build();
    }
}
