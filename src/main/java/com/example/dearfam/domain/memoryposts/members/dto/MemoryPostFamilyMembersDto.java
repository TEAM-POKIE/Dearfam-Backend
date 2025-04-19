package com.example.dearfam.domain.memoryposts.members.dto;

import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostFamilyMembersDto {

    private Long familyMemberId;
    private String nickname;
    // TODO : 참여한 가족의 프로필 이미지를 건네줘야함

    public static MemoryPostFamilyMembersDto from(MemoryPostFamilyMembers memoryPostFamilyMembers) {
        return MemoryPostFamilyMembersDto.builder()
                .familyMemberId(memoryPostFamilyMembers.getJoinedFamilyMember().getId())
                .nickname(memoryPostFamilyMembers.getJoinedFamilyMember().getUserNickname())
                .build();
    }

    public static List<MemoryPostFamilyMembersDto> from(List<MemoryPostFamilyMembers> memoryPostFamilyMembers) {
        return memoryPostFamilyMembers.stream()
                .map(MemoryPostFamilyMembersDto::from)
                .collect(Collectors.toList());
    }

}
