package com.example.dearfam.domain.animatedphoto.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.family.entity.Family;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "animate_photo")
public class AnimatePhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animated_photo_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Column(name = "animate_photo_key")
    private String animatePhoto;

    @Builder
    public AnimatePhoto(Family family, String animatePhoto) {
        this.family = family;
        this.animatePhoto = animatePhoto;
    }

}
