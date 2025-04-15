package com.example.dearfam.domain.memoryposts.memorypost.repository;

import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryPostRepository extends JpaRepository<MemoryPost, Long> {
}
