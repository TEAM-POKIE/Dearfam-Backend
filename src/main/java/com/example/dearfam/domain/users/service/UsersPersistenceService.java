package com.example.dearfam.domain.users.service;

import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsersPersistenceService {
    private final UsersRepository usersRepository;

    @Transactional
    public Users createUser(String userNickName, String userRole, String userFamilyRole, 
                          Boolean isFamilyRoomManager, String profileImage) {
        // userRole 검증 로직
        if ("admin".equalsIgnoreCase(userRole)) {
            throw UsersErrorCode.INVALID_ROLE.defaultException();
        }
        if (userRole == null) {
            userRole = "USER";
        }
        
        // isFamilyRoomManager 검증 로직
        if (isFamilyRoomManager == null) {
            isFamilyRoomManager = false;
        }
        
        Users user = Users.builder()
                .userNickName(userNickName)
                .userRole(userRole)
                .userFamilyRole(userFamilyRole)
                .isFamilyRoomManager(isFamilyRoomManager)
                .profileImage(profileImage)
                .refreshToken(null)
                .build();
        
        return usersRepository.save(user);
    }
}
