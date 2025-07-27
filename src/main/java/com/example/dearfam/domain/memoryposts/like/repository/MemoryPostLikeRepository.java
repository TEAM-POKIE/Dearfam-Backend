package com.example.dearfam.domain.memoryposts.like.repository;

import com.example.dearfam.domain.memoryposts.like.entity.MemoryPostLike;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryPostLikeRepository extends JpaRepository<MemoryPostLike, Long> {
    MemoryPostLike findByLikedUserAndMemoryPost(Users likedUser, MemoryPost memoryPost);
    boolean existsByLikedUserAndMemoryPost(Users likedUser, MemoryPost memoryPost);
}
