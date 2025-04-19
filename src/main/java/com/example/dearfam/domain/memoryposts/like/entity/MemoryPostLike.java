package com.example.dearfam.domain.memoryposts.like.entity;

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
@Table(
        name = "memory_post_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"liked_user_id", "memory_post_id"})
)
public class MemoryPostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_post_like_id", nullable = false)
    private Long memoryPostLikeId;

    @ManyToOne
    @JoinColumn(name = "memory_post_id", nullable = false)
    private MemoryPost memoryPost;

    @ManyToOne
    @JoinColumn(name = "liked_user_id", nullable = false)
    private Users likedUser;

    @Column(nullable = false)
    private boolean liked;

    @Builder
    public MemoryPostLike(MemoryPost memoryPost, Users likedUser, boolean liked) {
        this.memoryPost = memoryPost;
        this.likedUser = likedUser;
        this.liked = liked;
    }

}
