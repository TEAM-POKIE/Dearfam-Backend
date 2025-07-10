package com.example.dearfam.domain.memoryposts.image.repository;

import com.example.dearfam.domain.memoryposts.image.entity.MemoryPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryPostImageRepository extends JpaRepository<MemoryPostImage, Long> {
}
