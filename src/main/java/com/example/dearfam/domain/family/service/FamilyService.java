package com.example.dearfam.domain.family.service;

import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.family.repository.FamilyRepository;
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
    public FamilyDto createFamily(String FamilyName, Long userId) {
        // 카카오 로그인 이후임
        // 가족 이름 작성 후 family 디비에 저장 -> 가족 id 현재 로그인한 유저 정보에 set
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        if (user.getFamily() != null) {
            throw FamilyErrorCode.FAMILY_ALREADY_EXISTS.defaultException();
        }

        Family family = Family.builder()
                .familyName(FamilyName)
                .build();
        familyRepository.save(family);


        user.setIsFamilyRoomManager(true);
        user.setFamily(family);
        usersRepository.save(user);

        return FamilyDto.from(family);
    }

}
