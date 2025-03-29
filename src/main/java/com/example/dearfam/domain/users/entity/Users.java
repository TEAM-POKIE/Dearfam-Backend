package com.example.dearfam.domain.users.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class Users extends BaseTimeEntity {  // BaseTimeEntity에 생성일(createdAt)과 수정일(modifiedAt) 포함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // TODO : family ID foreign key로 등록하기

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "user_family_role", nullable = false)
    private String userFamilyRole;

    @Column(name = "is_family_room_manager")
    private Boolean isFamilyRoomManager;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "refresh_token", nullable = false, length = 1024)
    private String refreshToken;

    @Builder
    public Users(String userNickName, String userRole, String userFamilyRole, Boolean isFamilyRoomManager,
                 String profileImage, String refreshToken) {
        this.userNickname = userNickName;
        this.userRole = userRole;
        this.userFamilyRole = userFamilyRole;
        this.isFamilyRoomManager = isFamilyRoomManager;
        this.profileImage = profileImage;
        this.refreshToken = refreshToken;
    }

}
