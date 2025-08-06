package com.example.dearfam.domain.auth.service;

import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.auth.kakao.KakaoClient;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.family.repository.FamilyRepository;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final FamilyRepository familyRepository;
    private final KakaoClient kakaoClient;
    private final S3Service s3Service;

    @Transactional
    public void logout(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        user.setRefreshToken(null); // DB에 저장된 Refresh Token을 무효화
        usersRepository.save(user);

        log.info("로그아웃 처리 완료. userId: {}", userId);
    }


    @Transactional
    public void withdrawUser(Long userId) {
        log.info("[회원 탈퇴] 시작 - userId: {}", userId);

        // 1. 사용자 정보 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        // 2. 카카오 연결 끊기 (가장 먼저 수행하는 것이 안전)
        kakaoClient.unlinkUser(user.getSocialUserId());

        // 3. 가족 호출
        Family family = user.getFamily();

        // 4. 가족이 없는 경우 오류
        if (family == null) {
            log.error("소속된 가족이 없습니다. userId: {}", userId);
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        int memberCount = usersRepository.countByFamily(family);

        if (memberCount > 1) {
            // 마지막 멤버가 아닌 경우: 사용자 정보만 삭제
            log.info("가족에 다른 멤버가 남아있어 사용자 정보만 삭제합니다. familyId: {}, 남은 멤버 수: {}", family.getId(), memberCount - 1);
            deleteProfileImageFromS3(user.getProfileImage());
            usersRepository.delete(user); // Family 데이터는 그대로 유지됨
        } else {
            // 마지막 멤버인 경우: 모든 가족 데이터 삭제
            log.warn("가족의 마지막 멤버입니다. 모든 가족 데이터를 삭제합니다. familyId: {}", family.getId());

            // S3에서 삭제할 모든 파일 키 수집
            List<String> keysToDelete = collectAllFamilyS3Keys(family);

            // S3에서 일괄 삭제
            s3Service.deleteAllByKeys(keysToDelete);

            // DB에서 Family를 삭제하면, Cascade 설정에 따라 모든 관련 데이터가 연쇄적으로 삭제됨
            familyRepository.delete(family);
            log.info("Family 및 모든 연관 데이터(Users, Posts, Diaries, Videos) DB에서 삭제 완료.");
        }
        log.info("[회원 탈퇴] 완료 - userId: {}", userId);
    }

    private List<String> collectAllFamilyS3Keys(Family family) {
        List<String> keysToDelete = new ArrayList<>();

        // 1. 가족 구성원들의 프로필 이미지 키 수집
        family.getUsers().forEach(member -> {
            if (member.getProfileImage() != null && !member.getProfileImage().startsWith("http")) {
                keysToDelete.add(member.getProfileImage());
            }
        });

        // 2. 그림일기 이미지 키 수집
        family.getDiaryBooks().forEach(diary -> keysToDelete.add(diary.getDiaryImage()));

        // 3. 영상화된 사진 키 수집
        family.getAnimatePhotos().forEach(video -> keysToDelete.add(video.getAnimatePhoto()));

        // 4. 추억 게시글의 모든 이미지 키 수집
        family.getMemoryPosts().forEach(post ->
                post.getMemoryPostImages().forEach(image ->
                        keysToDelete.add(image.getImageKey())
                )
        );

        return keysToDelete;
    }


    private void deleteProfileImageFromS3(String key) {
        if (key != null && !key.startsWith("http")) {
            s3Service.delete(key);
        }
    }
}
