package com.example.dearfam.domain.users.repository;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.users.entity.UserFamilyRole;
import com.example.dearfam.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserNickname(String userNickname);
    boolean existsByFamilyAndUserFamilyRole(Family family, UserFamilyRole userFamilyRole);
    List<Users> findAllByFamily(Family family);
    Optional<Users> findByEmail(String email);
    List<Users> findAllByFamilyId(Long familyId);
}
