package com.example.dearfam.domain.diary.entity;

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
@Table(name = "diary_book")
public class DiaryBook extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_book_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Column(name = "diary_image_key", nullable = false)
    private String diaryImage;

    @Builder
    public DiaryBook(Family family, String diaryImage) {
        this.family = family;
        this.diaryImage = diaryImage;
    }

}
