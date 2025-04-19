package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.dto.FamilyMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetMemoryPostFamilyMembersResponse {
    private Long postId;
    private Long writerId;
    private List<FamilyMemberDto> participants;

    public static GetMemoryPostFamilyMembersResponse from(MemoryPost memoryPost, List<FamilyMemberDto> participants) {
        return GetMemoryPostFamilyMembersResponse.builder()
                .postId(memoryPost.getId())
                .writerId(memoryPost.getWriter().getId())
                .participants(participants)
                .build();
    }
}
