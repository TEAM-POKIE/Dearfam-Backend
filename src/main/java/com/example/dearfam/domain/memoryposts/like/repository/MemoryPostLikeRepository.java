package com.example.dearfam.domain.memoryposts.like.repository;

import com.example.dearfam.domain.memoryposts.like.entity.MemoryPostLike;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryPostLikeRepository extends JpaRepository<MemoryPostLike, Long> {
    MemoryPostLike findMemoryPostLikeByLikedUserAndMemoryPost(Users likedUser, MemoryPost memoryPost);
}
