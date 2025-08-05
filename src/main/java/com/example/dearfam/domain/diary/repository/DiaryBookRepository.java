package com.example.dearfam.domain.diary.repository;

import com.example.dearfam.domain.diary.entity.DiaryBook;
import com.example.dearfam.domain.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryBookRepository extends JpaRepository<DiaryBook, Long> {

    List<DiaryBook> findAllByFamilyOrderByCreatedAtDesc(Family family);

}
