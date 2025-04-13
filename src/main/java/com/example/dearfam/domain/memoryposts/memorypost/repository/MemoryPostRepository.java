package com.example.dearfam.domain.memoryposts.memorypost.repository;

import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryPostRepository extends JpaRepository<MemoryPost, Long> {
}
