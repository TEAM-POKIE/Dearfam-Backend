package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetUpdatedPostResponse {
    private Long postId;
    private String title;
    private String content;

    public static GetUpdatedPostResponse from(Long postId, String title, String content) {
        return GetUpdatedPostResponse.builder()
                .postId(postId)
                .title(title)
                .content(content)
                .build();
    }
}
