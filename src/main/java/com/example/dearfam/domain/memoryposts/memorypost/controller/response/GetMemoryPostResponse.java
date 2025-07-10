package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import com.example.dearfam.domain.memoryposts.image.dto.MemoryPostImageDto;
import com.example.dearfam.domain.memoryposts.members.dto.MemoryPostFamilyMembersDto;
import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetMemoryPostResponse {
    // 게시글을 만들거나  쓴 게시글을 바로 볼 텐데, 그 때 받아야 하는 정보들
    private Long writerId;
    private String title;
    private String content;
    private LocalDate memoryDate;
    private boolean isLiked;
    private List<MemoryPostFamilyMembersDto> participantFamilyMembers;
    private List<MemoryPostImageDto> imageUrls;

    public static GetMemoryPostResponse from(MemoryPostDto memoryPostDto,
                                             List<MemoryPostFamilyMembersDto> participantFamilyMembers,
                                             List<MemoryPostImageDto> images,
                                             boolean isLiked) {
        // image를 순서대로 배치
        List<MemoryPostImageDto> sortedImages = images.stream()
                .sorted(Comparator.comparingInt(MemoryPostImageDto::getImageOrder))
                .toList();

        return GetMemoryPostResponse.builder()
                .writerId(memoryPostDto.getWriter().getId())
                .title(memoryPostDto.getMemoryPostTitle())
                .content(memoryPostDto.getMemoryPostContent())
                .memoryDate(memoryPostDto.getMemoryDate())
                .isLiked(isLiked)
                .participantFamilyMembers(participantFamilyMembers)
                .imageUrls(sortedImages)
                .build();
    }

}
