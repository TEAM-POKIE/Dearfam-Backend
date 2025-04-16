package com.example.dearfam.domain.memoryposts.comment.entity;

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
@Table(name = "memory_post_comment")
public class MemoryPostComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_post_comment_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memory_post_id", nullable = false)
    private MemoryPost memoryPost;

    @ManyToOne
    @JoinColumn(name = "comment_writer_id", nullable = false)
    private Users commentWriter;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Builder
    public MemoryPostComment(MemoryPost memoryPost, Users commentWriter, String commentContent) {
        this.memoryPost = memoryPost;
        this.commentWriter = commentWriter;
        this.commentContent = commentContent;
    }


}
