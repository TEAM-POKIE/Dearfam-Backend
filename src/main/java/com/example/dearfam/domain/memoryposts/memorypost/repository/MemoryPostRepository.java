package com.example.dearfam.domain.memoryposts.memorypost.repository;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryPostRepository extends JpaRepository<MemoryPost, Long> {
    List<MemoryPost> findAllByFamilyOrderByMemoryDateDesc(Family family);
    List<MemoryPost> findTop10ByFamilyOrderByMemoryDateDesc(Family family);
}
