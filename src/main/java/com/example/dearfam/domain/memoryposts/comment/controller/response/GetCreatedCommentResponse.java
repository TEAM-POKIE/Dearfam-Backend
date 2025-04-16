package com.example.dearfam.domain.memoryposts.comment.controller.response;

import com.example.dearfam.domain.memoryposts.comment.dto.MemoryPostCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetCreatedCommentResponse {
    private Long postId;
    private Long writerId;
    private String content;

    public static GetCreatedCommentResponse from(MemoryPostCommentDto memoryPostCommentDto) {
        return GetCreatedCommentResponse.builder()
                .postId(memoryPostCommentDto.getMemoryPost().getId())
                .writerId(memoryPostCommentDto.getMemoryPost().getWriter().getId())
                .content(memoryPostCommentDto.getCommentContent())
                .build();
    }
}
