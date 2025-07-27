package com.example.dearfam.domain.memoryposts.memorypost.repository;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryPostRepository extends JpaRepository<MemoryPost, Long> {
    List<MemoryPost> findAllByFamilyOrderByMemoryDateDescCreatedAtDesc(Family family);
    List<MemoryPost> findTop10ByFamilyOrderByMemoryDateDescCreatedAtDesc(Family family);
}
