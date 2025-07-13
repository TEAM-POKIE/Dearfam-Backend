package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import com.example.dearfam.domain.memoryposts.members.dto.MemoryPostFamilyMembersDto;
import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetRecentMemoryPostResponse {
    // 응답으로 넘겨야 하는 정보 : 게시글의 첫 번째 이미지, 제목, 내용, 게시글 참여 가족 구성원 리스트, 좋아요 여부, 댓글 수
    private Long postId;
    private String title;
    private String content;
    private Integer commentCount;
    private LocalDate memoryDate;
    private String thumbnailUrl;
    private boolean isLiked;
    private List<MemoryPostFamilyMembersDto> participants;

    public static GetRecentMemoryPostResponse from(MemoryPostDto memoryPostDto,
                                                   List<MemoryPostFamilyMembersDto> participants,
                                                   String thumbnailUrl,
                                                   boolean isLiked) {
        return GetRecentMemoryPostResponse.builder()
                .postId(memoryPostDto.getId())
                .title(memoryPostDto.getMemoryPostTitle())
                .content(memoryPostDto.getMemoryPostContent())
                .commentCount(memoryPostDto.getMemoryPostCommentCount())
                .memoryDate(memoryPostDto.getMemoryDate())
                .thumbnailUrl(thumbnailUrl)
                .isLiked(isLiked)
                .participants(participants)
                .build();
    }

    public static List<GetRecentMemoryPostResponse> from(List<MemoryPostDto> memoryPostDtoList,
                                                         Map<Long, List<MemoryPostFamilyMembersDto>> participantsMap,
                                                         Map<Long, String> thumbnailUrlMap,
                                                         Map<Long, Boolean> isLikedMap) {
        return memoryPostDtoList.stream()
                .map(postDto -> GetRecentMemoryPostResponse.from(
                        postDto,
                        participantsMap.getOrDefault(postDto.getId(), List.of()),
                        thumbnailUrlMap.get(postDto.getId()),
                        isLikedMap.get(postDto.getId())
                ))
                .collect(Collectors.toList());
    }


}
