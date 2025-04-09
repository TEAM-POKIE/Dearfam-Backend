package com.example.dearfam.domain.users.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.family.entity.Family;
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
    @Column(name = "user_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_family_role")
    private UserFamilyRole userFamilyRole;

    // 아래 세 개의 컬럼은 소셜 로그인 시 받는 정보들
    @Column(name = "user_email", nullable = false, length = 50)
    private String email;

    @Column(name = "social_provider", nullable = false, length = 20)
    private String socialProvider;

    @Column(name = "social_user_id", nullable = false)
    private String socialUserId;

    @Column(name = "is_family_room_manager", nullable = false)
    private Boolean isFamilyRoomManager;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "refresh_token", length = 1024)
    private String refreshToken;

    @Builder
    public Users(Family family, String userNickName, String userRole, UserFamilyRole userFamilyRole,
                 String email, String socialProvider, String socialUserId,
                 Boolean isFamilyRoomManager,String profileImage, String refreshToken) {
        this.family = family;
        this.userNickname = userNickName;
        this.userRole = userRole;
        this.userFamilyRole = userFamilyRole;
        this.email = email;
        this.socialProvider = socialProvider;
        this.socialUserId = socialUserId;
        this.isFamilyRoomManager = isFamilyRoomManager;
        this.profileImage = profileImage;
        this.refreshToken = refreshToken;
    }

}
