package com.example.dearfam.domain.family.service;

import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.family.repository.FamilyRepository;
import com.example.dearfam.domain.users.entity.UserFamilyRole;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public FamilyDto createFamily(String familyName, Long userId) {
        // 카카오 로그인 이후임
        // 가족 이름 작성 후 family 디비에 저장 -> 가족 id 현재 로그인한 유저 정보에 set
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        if (user.getFamily() != null) {
            throw FamilyErrorCode.FAMILY_ALREADY_EXISTS.defaultException();
        }

        if(familyName == null || familyName.isEmpty()) {
            throw FamilyErrorCode.INVALID_FAMILY_NAME.defaultException();
        }

        Family family = Family.builder()
                .familyName(familyName)
                .build();
        familyRepository.save(family);


        user.setIsFamilyRoomManager(true);
        user.setFamily(family);
        usersRepository.save(user);

        return FamilyDto.from(family);
    }

    @Transactional
    public void assignUserFamilyRole(Long userId, UserFamilyRole userFamilyRole) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        Family family = user.getFamily();
        if (family == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        // 부모 역할 제한 -> 아빠, 엄마 한 명씩으로 제한
        if (userFamilyRole.isParent()) {
            boolean fatherExists = userFamilyRole == UserFamilyRole.FATHER && usersRepository.existsByFamilyAndUserFamilyRole(family, UserFamilyRole.FATHER);
            boolean motherExists = userFamilyRole == UserFamilyRole.MOTHER && usersRepository.existsByFamilyAndUserFamilyRole(family, UserFamilyRole.MOTHER);

            if (fatherExists || motherExists) {
                throw FamilyErrorCode.PARENT_LIMIT_EXCEEDED.defaultException();
            }
            family.setParentCount(family.getParentCount() + 1);
        }

        // 자녀 수 5명으로 제한
        if (userFamilyRole.isChild()) {
            if (family.getChildCount() >= 5) {
                throw FamilyErrorCode.CHILD_LIMIT_EXCEEDED.defaultException();
            }
            family.setChildCount(family.getChildCount() + 1);
        }

        user.setUserFamilyRole(userFamilyRole);
        usersRepository.save(user);
        familyRepository.save(family);
    }

    @Transactional
    public FamilyDto addUserToFamily(Long userId, Long familyId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        if (user.getFamily() != null) {
            throw FamilyErrorCode.FAMILY_ALREADY_EXISTS.defaultException();
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyErrorCode.FAMILY_NOT_FOUND::defaultException);

        user.setFamily(family);
        usersRepository.save(user);
        return FamilyDto.from(family);
    }


}
