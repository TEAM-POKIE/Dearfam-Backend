package com.example.dearfam.domain.memoryposts.comment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetAllCommentResponse {
    private Long commentId;
    private Long commentWriterId;
    private String commentWriterName;
    private String content;
    private String commentWriterProfileImage;

}
