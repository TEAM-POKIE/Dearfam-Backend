package com.example.dearfam.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "diary_page")
public class DiaryPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_page_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_book_id", nullable = false)
    private DiaryBook diaryBook;

    @Column(name = "diary_page_number", nullable = false)
    private Integer diaryPageNumber;

    @Column(name = "diary_page_url", nullable = false)
    private String diaryPageUrl;

    @Builder
    public DiaryPage(DiaryBook diaryBook, Integer diaryPageNumber, String diaryPageUrl) {
        this.diaryBook = diaryBook;
        this.diaryPageNumber = diaryPageNumber;
        this.diaryPageUrl = diaryPageUrl;
    }

}
