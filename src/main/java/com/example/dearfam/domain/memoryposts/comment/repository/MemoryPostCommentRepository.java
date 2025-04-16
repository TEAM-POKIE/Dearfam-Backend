package com.example.dearfam.domain.memoryposts.comment.repository;

import com.example.dearfam.domain.memoryposts.comment.entity.MemoryPostComment;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryPostCommentRepository extends JpaRepository<MemoryPostComment, Long> {
    List<MemoryPostComment> findAllByMemoryPostOrderByCreatedAtAsc(MemoryPost memoryPost);
}
