package com.example.dearfam.domain.memoryposts.comment.mapper;

import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.memoryposts.comment.controller.response.GetAllCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.controller.response.GetCreatedCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.entity.MemoryPostComment;
import com.example.dearfam.domain.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemoryPostCommentMapper {

    private final S3Service s3Service;

    public GetCreatedCommentResponse toGetCreatedCommentResponse(MemoryPostComment memoryPostcomment) {
        Users writer = memoryPostcomment.getCommentWriter();
        return GetCreatedCommentResponse.builder()
                .postId(memoryPostcomment.getMemoryPost().getId())
                .content(memoryPostcomment.getCommentContent())
                .commentWriterId(writer.getId())
                .commentWriterName(writer.getUserNickname())
                .commentWriterProfileImage(resolveProfileImageUrl(writer.getProfileImage()))
                .build();
    }

    public GetAllCommentResponse toGetAllCommentResponse(MemoryPostComment comment) {
        Users writer = comment.getCommentWriter();
        return GetAllCommentResponse.builder()
                .commentId(comment.getId())
                .commentWriterId(writer.getId())
                .commentWriterName(writer.getUserNickname())
                .content(comment.getCommentContent())
                .commentWriterProfileImage(resolveProfileImageUrl(writer.getProfileImage()))
                .build();
    }

    private String resolveProfileImageUrl(String image) {
        if (image == null || image.isBlank()) return null;
        if (image.startsWith("http")) return image;
        return s3Service.generateUrlFromKey(image);
    }
}
