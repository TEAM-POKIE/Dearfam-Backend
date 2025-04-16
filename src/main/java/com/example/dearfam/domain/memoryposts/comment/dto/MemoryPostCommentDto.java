package com.example.dearfam.domain.memoryposts.comment.dto;

import com.example.dearfam.domain.memoryposts.comment.entity.MemoryPostComment;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostCommentDto {
    private final Long commentId;
    private final MemoryPost memoryPost;
    private final Users commentWriter;
    private final String commentContent;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MemoryPostCommentDto from(MemoryPostComment memoryPostComment) {
        return MemoryPostCommentDto.builder()
                .commentId(memoryPostComment.getId())
                .memoryPost(memoryPostComment.getMemoryPost())
                .commentWriter(memoryPostComment.getCommentWriter())
                .commentContent(memoryPostComment.getCommentContent())
                .createdAt(memoryPostComment.getCreatedAt())
                .updatedAt(memoryPostComment.getUpdatedAt())
                .build();
    }
}
