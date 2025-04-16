package com.example.dearfam.domain.memoryposts.members.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "memory_post_family_members")
public class MemoryPostFamilyMembers extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joined_family_member_id", nullable = false)
    private Users joinedFamilyMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_post_id", nullable = false)
    private MemoryPost memoryPost;

    @Builder
    public MemoryPostFamilyMembers(Users joinedFamilyMember, MemoryPost memoryPost) {
        this.joinedFamilyMember = joinedFamilyMember;
        this.memoryPost = memoryPost;
    }

}
