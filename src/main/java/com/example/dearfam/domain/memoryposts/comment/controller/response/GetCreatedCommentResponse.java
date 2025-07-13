package com.example.dearfam.domain.memoryposts.comment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetCreatedCommentResponse {
    private Long postId;
    private String content;
    private Long commentWriterId;
    private String commentWriterName;
    private String commentWriterProfileImage;

}
