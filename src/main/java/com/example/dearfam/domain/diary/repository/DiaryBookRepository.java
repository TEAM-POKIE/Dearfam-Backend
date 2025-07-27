package com.example.dearfam.domain.diary.repository;

import com.example.dearfam.domain.diary.entity.DiaryBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryBookRepository extends JpaRepository<DiaryBook, Long> {
}
