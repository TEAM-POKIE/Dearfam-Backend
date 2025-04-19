package com.example.dearfam.domain.memoryposts.comment.controller.response;

import com.example.dearfam.domain.memoryposts.comment.dto.MemoryPostCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetAllCommentResponse {
    private Long commentWriterId;
    private String commentWriterName;
    private String content;
    // TODO : 추후 이미지 처리 해야함.

    public static GetAllCommentResponse from(MemoryPostCommentDto memoryPostCommentDto) {
        return GetAllCommentResponse.builder()
                .commentWriterId(memoryPostCommentDto.getCommentWriter().getId())
                .commentWriterName(memoryPostCommentDto.getCommentWriter().getUserNickname())
                .content(memoryPostCommentDto.getCommentContent())
                .build();
    }

    public static List<GetAllCommentResponse> from(List<MemoryPostCommentDto> memoryPostCommentDtoList) {
        return memoryPostCommentDtoList.stream()
                .map(GetAllCommentResponse::from)
                .collect(Collectors.toList());
    }
}
