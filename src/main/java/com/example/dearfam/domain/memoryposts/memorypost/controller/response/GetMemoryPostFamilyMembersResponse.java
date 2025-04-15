package com.example.dearfam.domain.memoryposts.memorypost.controller.response;

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
    private List<FamilyMemberDto> participants;

    public static GetMemoryPostFamilyMembersResponse from(Long postId, List<FamilyMemberDto> participants) {
        return GetMemoryPostFamilyMembersResponse.builder()
                .postId(postId)
                .participants(participants)
                .build();
    }
}
