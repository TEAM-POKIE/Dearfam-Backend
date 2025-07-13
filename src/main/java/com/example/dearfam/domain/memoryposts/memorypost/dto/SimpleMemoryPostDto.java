package com.example.dearfam.domain.memoryposts.memorypost.dto;

import com.example.dearfam.domain.memoryposts.image.entity.MemoryPostImage;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class SimpleMemoryPostDto {
    private Long postId;
    private LocalDate memoryDate;
    private String thumbnailUrl;

    public static SimpleMemoryPostDto from(MemoryPost memoryPost, String thumbnailUrl) {
        return SimpleMemoryPostDto.builder()
                .postId(memoryPost.getId())
                .memoryDate(memoryPost.getMemoryDate())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
