    package com.example.dearfam.domain.diary.service;

    import com.example.dearfam.common.entity.UploadDirectory;
    import com.example.dearfam.common.proxy.ProxyUrlBuilder;
    import com.example.dearfam.common.service.S3Service;
    import com.example.dearfam.domain.diary.controller.request.DiaryGenerateRequest;
    import com.example.dearfam.domain.diary.controller.response.GetDiaryUrlResponse;
    import com.example.dearfam.domain.diary.controller.response.GetDiaryResponse;
    import com.example.dearfam.domain.diary.controller.response.GetSavedDiaryResponse;
    import com.example.dearfam.domain.diary.dto.DiaryContentDto;
    import com.example.dearfam.domain.diary.entity.DiaryBook;
    import com.example.dearfam.domain.diary.exception.DiaryErrorCode;
    import com.example.dearfam.domain.diary.repository.DiaryBookRepository;
    import com.example.dearfam.domain.family.entity.Family;
    import com.example.dearfam.domain.family.exception.FamilyErrorCode;
    import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
    import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
    import com.example.dearfam.domain.users.entity.Users;
    import com.example.dearfam.domain.users.exception.UsersErrorCode;
    import com.example.dearfam.domain.users.repository.UsersRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.*;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.client.RestClientException;
    import org.springframework.web.client.RestTemplate;
    import org.springframework.web.multipart.MultipartFile;

    import java.time.LocalDate;
    import java.time.format.TextStyle;
    import java.util.*;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class DiaryBookService {

        private final RestTemplate restTemplate;
        private final MemoryPostRepository memoryPostRepository;
        private final UsersRepository usersRepository;
        private final S3Service s3Service;
        private final DiaryBookRepository diaryBookRepository;
        private final ProxyUrlBuilder proxyUrlBuilder;

        @Value("${ai.server.url}")
        private String aiServerUrl;

        public GetDiaryResponse generateDiaryBook(DiaryGenerateRequest request) {
            // postId 리스트 가져오기
            Long postId = request.getPostId();
            log.info("[그림일기 생성] 요청 postId: {}", postId);

            if (postId == null) {
                log.warn("[그림일기 생성] postId가 null입니다.");
                throw DiaryErrorCode.POST_ID_REQUIRED.defaultException();
            }
            // postId로 MemoryPost 조회 (게시글이 없으면 예외 발생)
            MemoryPost post = memoryPostRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 생성] MemoryPost가 존재하지 않음 - postId: {}", postId);
                        return DiaryErrorCode.MEMORY_POST_NOT_FOUND.defaultException();
                    });

            // post 의 내용으로 그림일기 AI 호출
            String content = post.getMemoryPostContent();
            log.info("[그림일기 생성] MemoryPost content 가져오기 완료 - content 길이: {}", content.length());
            DiaryContentDto aiGeneratedContent = callAiServer(content);

            // AI가 생성한 이미지 URL을 프록시 URL로 교체합니다.
            if (aiGeneratedContent != null && aiGeneratedContent.getImageUrl() != null) {
                log.info("[그림일기 생성] proxy화 하지 않은 이미지 URL: {}", aiGeneratedContent.getImageUrl());
                String proxiedImageUrl = proxyUrlBuilder.toProxied(aiGeneratedContent.getImageUrl());

                // DiaryContentDto는 불변(immutable)이므로, 새로운 객체를 생성하여 값을 교체합니다.
                aiGeneratedContent = new DiaryContentDto(
                        aiGeneratedContent.getTitle(),
                        aiGeneratedContent.getContent(),
                        proxiedImageUrl
                );
            }

            LocalDate memoryDate = post.getMemoryDate();
            String weekday = memoryDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            log.info("[그림일기 생성] 완료 - 날짜: {}, 요일: {}", memoryDate, weekday);
            return GetDiaryResponse.from(memoryDate, weekday, aiGeneratedContent);
        }

        @Transactional
        public GetSavedDiaryResponse saveDiaryImage(Long userId, MultipartFile diaryImage) {
            log.info("[그림일기 저장] 사용자 ID: {}", userId);

            if (diaryImage == null || diaryImage.isEmpty()) {
                log.warn("[그림일기 저장] 이미지 파일이 비어 있음");
                throw DiaryErrorCode.IMAGE_FILE_EMPTY.defaultException();
            }

            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 저장] 사용자 조회 실패 - userId: {}", userId);
                        return UsersErrorCode.USER_NOT_FOUND.defaultException();
                    });
            Family family = user.getFamily();
            if (family == null) {
                log.warn("[그림일기 저장] 사용자에 대한 가족 정보가 없음 - userId: {}", userId);
                throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
            }

            log.info("[그림일기 저장] S3 업로드 시작 - familyId: {}", family.getId());
            String diaryImageKey = s3Service.upload(diaryImage, UploadDirectory.DIARY, family.getId(), "family");

            DiaryBook diaryBook = DiaryBook.builder()
                    .family(family)
                    .diaryImage(diaryImageKey)
                    .build();
            diaryBookRepository.save(diaryBook);
            log.info("[그림일기 저장] DiaryBook 저장 완료 - diaryBookId: {}", diaryBook.getId());

            String imageUrl = s3Service.generateUrlFromKey(diaryImageKey);
            log.info("[그림일기 저장] 최종 URL 반환 - {}", imageUrl);
            return GetSavedDiaryResponse.from(diaryBook.getId(), imageUrl);

        }

        @Transactional
        public void deleteDiary(Long userId, Long diaryBookId) {
            log.info("[그림일기 삭제] 사용자 ID: {}, diaryBookId: {}", userId, diaryBookId);
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 삭제] 사용자 조회 실패 - userId: {}", userId);
                        return UsersErrorCode.USER_NOT_FOUND.defaultException();
                    });

            DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 삭제] 일기 조회 실패 - diaryBookId: {}", diaryBookId);
                        return DiaryErrorCode.DIARY_BOOK_NOT_FOUND.defaultException();
                    });

            // 사용자가 소속된 가족과 일기의 가족이 다르면 삭제 불가
            if (!user.getFamily().getId().equals(diaryBook.getFamily().getId())) {
                log.warn("[그림일기 삭제] 권한 없음 - userFamilyId: {}, diaryFamilyId: {}",
                        user.getFamily().getId(), diaryBook.getFamily().getId());
                throw DiaryErrorCode.UNAUTHORIZED_DIARY_DELETE.defaultException();
            }

            s3Service.delete(diaryBook.getDiaryImage());
            diaryBookRepository.delete(diaryBook);
            log.info("[그림일기 삭제] 삭제 완료");

        }

        @Transactional(readOnly = true)
        public List<GetDiaryUrlResponse> getAllDiaries(Long userId) {
            log.info("[그림일기 전체 조회] 사용자 ID: {}", userId);

            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("[그림일기 전체 조회] 사용자 조회 실패 - userId: {}", userId);
                        return UsersErrorCode.USER_NOT_FOUND.defaultException();
                    });

            Family family = user.getFamily();
            if (family == null) {
                log.error("[그림일기 전체 조회] 사용자에 대한 가족 정보가 없음 - userId: {}", userId);
                throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
            }

            List<DiaryBook> diaryBookList = diaryBookRepository.findAllByFamilyOrderByCreatedAtDesc(family);
            log.info("[그림일기 전체 조회] 가족 ID {}에 대한 {}개의 그림일기 조회", family.getId(), diaryBookList.size());
            List<GetDiaryUrlResponse> responseList = new ArrayList<>();
            for (DiaryBook diaryBook : diaryBookList) {
                String imageUrl = s3Service.generateUrlFromKey(diaryBook.getDiaryImage());
                GetDiaryUrlResponse response = GetDiaryUrlResponse.from(diaryBook.getId(), imageUrl);
                responseList.add(response);
            }
            return responseList;
        }

        @Transactional(readOnly = true)
        public GetDiaryUrlResponse getDiary(Long userId, Long diaryBookId) {
            log.info("[그림일기 단일 조회] 사용자 ID: {}, diaryId: {}", userId, diaryBookId);

            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 단일 조회] 사용자 조회 실패 - userId: {}", userId);
                        return UsersErrorCode.USER_NOT_FOUND.defaultException();
                    });
            DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                    .orElseThrow(() -> {
                        log.warn("[그림일기 단건 조회] 일기 조회 실패 - diaryBookId: {}", diaryBookId);
                        return DiaryErrorCode.DIARY_BOOK_NOT_FOUND.defaultException();
                    });

            // 사용자가 소속된 가족과 일기의 가족이 다르면 삭제 불가
            if (!user.getFamily().getId().equals(diaryBook.getFamily().getId())) {
                log.warn("[그림일기 단건 조회] 권한 없음 - userFamilyId: {}, diaryFamilyId: {}",
                        user.getFamily().getId(), diaryBook.getFamily().getId());
                throw DiaryErrorCode.UNAUTHORIZED_DIARY_ACCESS.defaultException();
            }

            String imageUrl = s3Service.generateUrlFromKey(diaryBook.getDiaryImage());
            log.info("[그림일기 단건 조회] 완료 - imageUrl: {}", imageUrl);
            return GetDiaryUrlResponse.from(diaryBook.getId(), imageUrl);
        }

        // 그림일기 호출 메서드
        private DiaryContentDto callAiServer(String content) {
            log.info("[AI 호출] 그림일기 생성 요청 전송 시작");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("user_text", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            try {
                // AI 서버로부터 List<DiaryContentDto> 타입의 응답을 직접 받기 위해 exchange 사용
                ResponseEntity<DiaryContentDto> response = restTemplate.postForEntity(
                        "https://" + aiServerUrl + "/generate-diary",
                        entity,
                        DiaryContentDto.class
                );
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    log.info("[AI 호출] 성공 - 응답 받음");
                    return response.getBody();
                } else {
                    log.error("[AI 호출] 실패 - 응답 코드: {}", response.getStatusCode());
                    throw DiaryErrorCode.AI_SERVER_FAILED.defaultException();
                }

            } catch (RestClientException e) {
                log.error("[AI 호출] 통신 오류 발생", e);
                throw DiaryErrorCode.AI_COMMUNICATION_ERROR.defaultException();
            }
        }


    }
