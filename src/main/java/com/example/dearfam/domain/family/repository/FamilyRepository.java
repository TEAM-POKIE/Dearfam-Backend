package com.example.dearfam.domain.family.repository;

import com.example.dearfam.domain.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {
}
