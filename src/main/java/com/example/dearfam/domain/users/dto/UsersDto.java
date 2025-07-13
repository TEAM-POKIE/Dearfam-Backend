package com.example.dearfam.domain.users.dto;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.users.entity.UserFamilyRole;
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
public class UsersDto {
    private Long id;
    private Family family;
    private String userNickName;
    private String userRole;
    private UserFamilyRole userFamilyRole;
    private Boolean isFamilyRoomManager;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
