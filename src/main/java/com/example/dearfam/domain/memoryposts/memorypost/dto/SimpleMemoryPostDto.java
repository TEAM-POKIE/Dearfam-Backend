package com.example.dearfam.domain.memoryposts.memorypost.dto;

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
//    private String imageUrl;

    public static SimpleMemoryPostDto from(MemoryPost memoryPost) {
        return SimpleMemoryPostDto.builder()
                .postId(memoryPost.getId())
                .memoryDate(memoryPost.getMemoryDate())
                .build();
    }

    public static List<SimpleMemoryPostDto> from(List<MemoryPost> memoryPosts) {
        return memoryPosts.stream().
                map(SimpleMemoryPostDto::from)
                .collect(Collectors.toList());
    }
}
