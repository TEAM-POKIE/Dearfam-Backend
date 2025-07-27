package com.example.dearfam.domain.diary.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
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

    @Column(name = "diary_title", nullable = false, length = 20)
    private String diaryTitle;

    @Column(name = "diary_cover_color", nullable = false)
    private DiaryCoverColor diaryCoverColor;

    @Builder
    public DiaryBook(String diaryTitle, DiaryCoverColor diaryCoverColor) {
        this.diaryTitle = diaryTitle;
        this.diaryCoverColor = diaryCoverColor;
    }

}
