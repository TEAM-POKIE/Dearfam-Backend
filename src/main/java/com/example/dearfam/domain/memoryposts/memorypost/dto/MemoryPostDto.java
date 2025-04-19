package com.example.dearfam.domain.memoryposts.memorypost.dto;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostDto {
    private Long id;
    private Family family;
    private Users writer;
    private String memoryPostTitle;
    private String memoryPostContent;
    private Integer memoryPostCommentCount;
    private Integer memoryPostImageCount;
    private LocalDate memoryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemoryPostDto from(MemoryPost memoryPost) {
        return MemoryPostDto.builder()
                .id(memoryPost.getId())
                .family(memoryPost.getFamily())
                .writer(memoryPost.getWriter())
                .memoryPostTitle(memoryPost.getMemoryPostTitle())
                .memoryPostContent(memoryPost.getMemoryPostContent())
                .memoryPostCommentCount(memoryPost.getMemoryPostCommentCount())
                .memoryPostImageCount(memoryPost.getMemoryPostImageCount())
                .memoryDate(memoryPost.getMemoryDate())
                .createdAt(memoryPost.getCreatedAt())
                .updatedAt(memoryPost.getUpdatedAt())
                .build();
    }

    public static List<MemoryPostDto> from(List<MemoryPost> memoryPostList) {
        return memoryPostList.stream()
                .map(MemoryPostDto::from)
                .collect(Collectors.toList());
    }

}
