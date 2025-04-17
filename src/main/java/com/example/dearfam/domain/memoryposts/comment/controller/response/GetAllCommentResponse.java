package com.example.dearfam.domain.memoryposts.comment.controller.response;

import com.example.dearfam.domain.memoryposts.comment.dto.MemoryPostCommentDto;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAllCommentResponse {
    private Long commentWriterId;
    private String content;
    private LocalDateTime createdAt;

    public static GetAllCommentResponse from(MemoryPostCommentDto memoryPostCommentDto) {
        return GetAllCommentResponse.builder()
                .commentWriterId(memoryPostCommentDto.getCommentWriter().getId())
                .content(memoryPostCommentDto.getCommentContent())
                .createdAt(memoryPostCommentDto.getCreatedAt())
                .build();
    }

    public static List<GetAllCommentResponse> from(List<MemoryPostCommentDto> memoryPostCommentDtoList) {
        return memoryPostCommentDtoList.stream()
                .map(GetAllCommentResponse::from)
                .collect(Collectors.toList());
    }
}
