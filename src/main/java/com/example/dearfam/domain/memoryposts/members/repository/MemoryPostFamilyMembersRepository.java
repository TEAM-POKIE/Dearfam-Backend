package com.example.dearfam.domain.memoryposts.members.repository;

import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryPostFamilyMembersRepository extends JpaRepository<MemoryPostFamilyMembers, Long> {
    List<MemoryPostFamilyMembers> findAllByMemoryPost(MemoryPost memoryPost);
}
