package com.example.dearfam.domain.users.mapper;

import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.memoryposts.members.dto.MemoryPostFamilyMembersDto;
import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.users.dto.FamilyMemberDto;
import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// 프로필 이미지 저장시 3가지 종류로 나뉘게 됨 - [null, 카카오 프로필 이미지, 사용자 설정 이미지(S3에 저장)]S3 이미지 저장 시에는,
// 전체 url 이 아닌 경로 key 만 저장하도록 해놓음. 그래서 Mapper 클래스를 통해 반환할 때 image를 반환할 수 있도록 해야함
public class UsersMapper {

    private final S3Service s3Service;

    public UsersDto toDto(Users user) {
        return UsersDto.builder()
                .id(user.getId())
                .family(user.getFamily())
                .userNickName(user.getUserNickname())
                .userRole(user.getUserRole())
                .userFamilyRole(user.getUserFamilyRole())
                .isFamilyRoomManager(user.getIsFamilyRoomManager())
                .profileImage(resolveProfileImageUrl(user.getProfileImage()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public FamilyMemberDto toFamilyMemberDto(Users familyMember) {
        return FamilyMemberDto.builder()
                .familyMemberId(familyMember.getId())
                .familyMemberNickname(familyMember.getUserNickname())
                .familyMemberRole(familyMember.getUserFamilyRole())
                .familyMemberProfileImage(resolveProfileImageUrl(familyMember.getProfileImage()))
                .build();
    }

    public MemoryPostFamilyMembersDto toMemoryPostFamilyMembersDto(MemoryPostFamilyMembers memoryPostFamilyMembers) {
        Users user = memoryPostFamilyMembers.getJoinedFamilyMember();
        return MemoryPostFamilyMembersDto.builder()
                .familyMemberId(user.getId())
                .nickname(user.getUserNickname())
                .profileImage(resolveProfileImageUrl(user.getProfileImage()))
                .build();
    }

    // 카카오 이미지인지, 사용자 설정 이미지인지 검사 - http 로 시작 시 카카오/profile 로 시작 시 사용자 설정 이미지
    private String resolveProfileImageUrl(String image) {
        if (image == null || image.isBlank()) return null;
        if (image.startsWith("http")) return image;
        return s3Service.generateUrlFromKey(image);
    }

}
