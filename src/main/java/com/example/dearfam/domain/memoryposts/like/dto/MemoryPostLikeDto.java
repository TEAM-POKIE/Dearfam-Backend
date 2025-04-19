package com.example.dearfam.domain.memoryposts.like.dto;

import com.example.dearfam.domain.memoryposts.like.entity.MemoryPostLike;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostLikeDto {
    private Long MemoryPostLikeId;
    private MemoryPost memoryPost;
    private Users likedUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemoryPostLikeDto from(MemoryPostLike memoryPostLike) {
        return MemoryPostLikeDto.builder()
                .MemoryPostLikeId(memoryPostLike.getMemoryPostLikeId())
                .memoryPost(memoryPostLike.getMemoryPost())
                .likedUser(memoryPostLike.getLikedUser())
                .createdAt(memoryPostLike.getCreatedAt())
                .updatedAt(memoryPostLike.getUpdatedAt())
                .build();
    }

}
