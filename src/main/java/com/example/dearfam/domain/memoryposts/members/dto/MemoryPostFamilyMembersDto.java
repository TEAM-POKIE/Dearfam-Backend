package com.example.dearfam.domain.memoryposts.members.dto;

import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostFamilyMembersDto {
    // 일단 만들어두긴 했는데, 필요없으면 삭제해도 됨.
    private Long id;
    private MemoryPost memoryPost;
    private Users joinedFamilyMember;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemoryPostFamilyMembersDto from(MemoryPostFamilyMembers memoryPostFamilyMembers) {
        return MemoryPostFamilyMembersDto.builder()
                .id(memoryPostFamilyMembers.getId())
                .memoryPost(memoryPostFamilyMembers.getMemoryPost())
                .joinedFamilyMember(memoryPostFamilyMembers.getJoinedFamilyMember())
                .createdAt(memoryPostFamilyMembers.getCreatedAt())
                .updatedAt(memoryPostFamilyMembers.getUpdatedAt())
                .build();
    }

}
