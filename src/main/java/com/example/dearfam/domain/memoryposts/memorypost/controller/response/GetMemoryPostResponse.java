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
public class GetMemoryPostResponse {
    // 게시글을 만들거나  쓴 게시글을 바로 볼 텐데, 그 때 받아야 하는 정보들
    private Long writerId;
    private String title;
    private String content;
    private LocalDate memoryDate;
    private List<MemoryPostFamilyMembersDto> participantFamilyMembers;

    // TODO : 추후 이미지는 id로 보낼지, 이미지 파일로 보낼지 한 번 고민

        return GetMemoryPostResponse.builder()
                .writerId(memoryPostDto.getWriter().getId())
                .title(memoryPostDto.getMemoryPostsTitle())
                .content(memoryPostDto.getMemoryPostsContent())
                .memoryDate(memoryPostDto.getMemoryDate())
                .participantFamilyMembers(participantFamilyMembers)
                .build();
    }

    public static List<GetMemoryPostResponse> from(List<MemoryPostDto> memoryPosts,
                                                   Map<Long, List<MemoryPostFamilyMembersDto>> memoryPostFamilyMembers,
        return memoryPosts.stream()
                .map(dto -> GetMemoryPostResponse.from(
                        dto,
                        memoryPostFamilyMembers.getOrDefault(dto.getId(), List.of()),
                ))
                .collect(Collectors.toList());
    }

}
