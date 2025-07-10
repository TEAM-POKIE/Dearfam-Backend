package com.example.dearfam.domain.memoryposts.image.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "memory_post_image")
public class MemoryPostImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_post_id", nullable = false)
    private MemoryPost memoryPost;

    @Column(name = "memory_post_image_key", nullable = false)
    private String imageKey;

    @Column(name = "image_order", nullable = false)
    private Integer imageOrder;

    @Builder
    public MemoryPostImage(String imageKey, Integer imageOrder) {
        this.imageKey = imageKey;
        this.imageOrder = imageOrder;
    }
}
