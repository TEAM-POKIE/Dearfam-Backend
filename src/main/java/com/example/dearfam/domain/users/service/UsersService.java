package com.example.dearfam.domain.users.service;

import com.example.dearfam.common.entity.UploadDirectory;
import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.users.dto.UsersDto;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.mapper.UsersMapper;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final S3Service s3Service;
    private final UsersMapper usersMapper;

    @Transactional(readOnly = true)
    public UsersDto getUserDtoById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        return usersMapper.toDto(user);
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
    public String updateUserProfileImage(Long userId, MultipartFile profileImage) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        String existingProfileImage = user.getProfileImage();
        log.info("기존 DB 저장 프로필 이미지: " + existingProfileImage);

        // 기존 이미지 삭제
        if (existingProfileImage != null && !existingProfileImage.startsWith("http")) {
            // 기존 S3 URL 일 경우, key 추출 후 삭제
            s3Service.delete(existingProfileImage);
            log.info("기존 이미지 삭제 완료 - image: {}", existingProfileImage);
        }

        // 새 이미지가 없으면 -> 업로드하지 않고 null 저장 (기본 이미지 처리)
        if (profileImage == null || profileImage.isEmpty()) {
            user.setProfileImage(null);
            usersRepository.save(user);
            log.info("프로필 이미지를 기본 이미지로 초기화함");
            return null;
        }

        // 새 이미지 업로드
        log.info("이미지 업로드 요청");
        String key = s3Service.upload(profileImage, UploadDirectory.PROFILES, userId);

        // DB 업데이트
        user.setProfileImage(key); // S3 Key 저장
        usersRepository.save(user);
        String imageUrl = s3Service.generateUrlFromKey(key);
        log.info("이미지 업데이트 완료 - URL: {}", imageUrl);

        return imageUrl;
    }
}
