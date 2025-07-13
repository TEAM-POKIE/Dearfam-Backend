package com.example.dearfam.domain.users.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetUpdatedProfileImageResponse {
    private String profileImageUrl;

    public static GetUpdatedProfileImageResponse from(String profileImageUrl) {
        return GetUpdatedProfileImageResponse.builder()
                .profileImageUrl(profileImageUrl)
                .build();
    }

}
