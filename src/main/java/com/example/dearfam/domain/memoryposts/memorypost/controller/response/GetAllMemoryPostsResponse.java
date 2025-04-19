package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import com.example.dearfam.domain.memoryposts.memorypost.dto.SimpleMemoryPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAllMemoryPostsResponse {

    private int year;
    private List<SimpleMemoryPostDto> posts;

    public static GetAllMemoryPostsResponse from(int year, List<SimpleMemoryPostDto> posts) {
        return GetAllMemoryPostsResponse.builder()
                .year(year)
                .posts(posts)
                .build();
    }

    public static List<GetAllMemoryPostsResponse> from(Map<Integer, List<SimpleMemoryPostDto>> postsGroupedByYear) {
        return postsGroupedByYear.entrySet().stream()
                .map(entry -> {
                    int year = entry.getKey();
                    List<SimpleMemoryPostDto> posts = entry.getValue();
                    return GetAllMemoryPostsResponse.from(year, posts);
                })
                .collect(Collectors.toList());
    }

}
