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
    private Long commentWriterId;
    private String commentWriterName;
    private String content;
    // TODO : 이미지 처리 해야함

    public static GetCreatedCommentResponse from(MemoryPostCommentDto memoryPostCommentDto) {
        return GetCreatedCommentResponse.builder()
                .postId(memoryPostCommentDto.getMemoryPost().getId())
                .commentWriterId(memoryPostCommentDto.getCommentWriter().getId())
                .commentWriterName(memoryPostCommentDto.getCommentWriter().getUserNickname())
                .content(memoryPostCommentDto.getCommentContent())
                .build();
    }
}
