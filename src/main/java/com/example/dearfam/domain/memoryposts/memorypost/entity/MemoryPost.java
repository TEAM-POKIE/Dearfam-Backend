package com.example.dearfam.domain.memoryposts.memorypost.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.memoryposts.comment.entity.MemoryPostComment;
import com.example.dearfam.domain.memoryposts.image.entity.MemoryPostImage;
import com.example.dearfam.domain.memoryposts.like.entity.MemoryPostLike;
import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "memory_post")
public class MemoryPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Users writer;

    @Column(name = "memory_post_title", nullable = false, length = 50)
    private String memoryPostTitle;

    @Column(name = "memory_post_content", nullable = false, length = 500)
    private String memoryPostContent;

    @Column(name = "memory_post_comment_count", nullable = false)
    private Integer memoryPostCommentCount;

    @Column(name = "memory_post_image_count", nullable = false)
    private Integer memoryPostImageCount;

    @Column(name = "memory_date", nullable = false)
    private LocalDate memoryDate;

    @OneToMany(mappedBy = "memoryPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryPostImage> memoryPostImages = new ArrayList<>();

    @OneToMany(mappedBy = "memoryPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryPostComment> memoryPostComments = new ArrayList<>();

    @OneToMany(mappedBy = "memoryPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryPostLike> memoryPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "memoryPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryPostFamilyMembers> memoryPostFamilyMembers = new ArrayList<>();

    @Builder
    public MemoryPost(Family family, Users writer, String memoryPostTitle, String memoryPostContent, Integer memoryPostCommentCount,
                      Integer memoryPostImageCount, LocalDate memoryDate) {
        this.family = family;
        this.writer = writer;
        this.memoryPostTitle = memoryPostTitle;
        this.memoryPostContent = memoryPostContent;
        this.memoryPostCommentCount = memoryPostCommentCount == null ? 0 : memoryPostCommentCount;
        this.memoryPostImageCount = memoryPostImageCount == null ? 0 : memoryPostImageCount;
        this.memoryDate = memoryDate;
    }

    // 양방향 관계 설정
    public void addImage(MemoryPostImage image) {
        this.memoryPostImages.add(image);
        image.setMemoryPost(this);
    }

}
