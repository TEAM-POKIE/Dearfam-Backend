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
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Column(name = "user_role", nullable = false)
    private String userRole;


    @Column(name = "is_family_room_manager")
    private Boolean isFamilyRoomManager;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "refresh_token", nullable = false, length = 1024)
    private String refreshToken;

    @Builder
                 String profileImage, String refreshToken) {
        this.userNickname = userNickName;
        this.userRole = userRole;
        this.userFamilyRole = userFamilyRole;
        this.isFamilyRoomManager = isFamilyRoomManager;
        this.profileImage = profileImage;
        this.refreshToken = refreshToken;
    }

}
