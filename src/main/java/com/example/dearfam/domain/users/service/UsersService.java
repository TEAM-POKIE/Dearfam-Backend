package com.example.dearfam.domain.users.service;

import com.example.dearfam.domain.family.controller.response.GetFamilyResponse;
import com.example.dearfam.domain.family.dto.FamilyDto;
import com.example.dearfam.domain.family.repository.FamilyRepository;
import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;

    @Transactional
    public UsersDto getUserDtoById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        return UsersDto.from(user);
    }

    @Transactional
    public void updateUserNickname(Long userId, String newNickname) {
        Users user = usersRepository.findById(userId).orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        Optional<Users> existedUser = usersRepository.findByUserNickname(newNickname);

        // 1. 같은 닉네임인데 자기 자신이 아닌 경우 → 중복
        if (existedUser.isPresent() && !existedUser.get().getId().equals(user.getId())) {
            throw UsersErrorCode.NICKNAME_ALREADY_EXISTS.defaultException();
        }

        // 2. 같은 닉네임으로 수정하는 경우 → 무의미
        if (newNickname.equals(user.getUserNickname())) {
            throw UsersErrorCode.NICKNAME_ALREADY_EXISTS.defaultException();
        }

        user.setUserNickname(newNickname);
        usersRepository.save(user);
    }

    @Transactional
    public GetFamilyResponse getUserFamily(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        FamilyDto familyDto = FamilyDto.from(user.getFamily());

        return GetFamilyResponse.from(familyDto);
    }
}
