package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetMemoryPostResponse {

    private Long postId;
    private Long familyId;
    private Long writerId;
    private String title;
    private String content;
    private LocalDate memoryDate;
//    private List<Long> participantFamilyMemberIds;

    // TODO : 추후 이미지는 id로 보낼지, 이미지 파일로 보낼지 한 번 고민

    public static GetMemoryPostResponse from(MemoryPostDto memoryPostDto) {
        return GetMemoryPostResponse.builder()
                .postId(memoryPostDto.getId())
                .writerId(memoryPostDto.getWriter().getId())
                .familyId(memoryPostDto.getFamily().getId())
                .title(memoryPostDto.getMemoryPostsTitle())
                .content(memoryPostDto.getMemoryPostsContent())
                .memoryDate(memoryPostDto.getMemoryDate())
                // TODO : 이건 나중에 그 참여 가족 엔티티 구성하고, dto 추가해서 구현
                .build();
    }

    public static List<GetMemoryPostResponse> from(List<MemoryPostDto> memoryPostDtos) {
        return memoryPostDtos.stream()
                .map(GetMemoryPostResponse::from)
                .collect(Collectors.toList());
    }
}
