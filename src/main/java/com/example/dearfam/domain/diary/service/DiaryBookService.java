    package com.example.dearfam.domain.diary.service;

    import com.example.dearfam.common.entity.UploadDirectory;
    import com.example.dearfam.common.service.S3Service;
    import com.example.dearfam.domain.diary.controller.request.DiaryGenerateRequest;
    import com.example.dearfam.domain.diary.controller.response.GetDiaryResponse;
    import com.example.dearfam.domain.diary.controller.response.GetSavedDiaryResponse;
    import com.example.dearfam.domain.diary.dto.DiaryContentDto;
    import com.example.dearfam.domain.diary.entity.DiaryBook;
    import com.example.dearfam.domain.diary.exception.DiaryErrorCode;
    import com.example.dearfam.domain.diary.exception.DiaryException;
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

        @Value("${ai.server.url}")
        private String aiServerUrl;

        public GetDiaryResponse generateDiaryBook(DiaryGenerateRequest request) {
            // postId 리스트 가져오기
            Long postId = request.getPostId();
            if (postId == null) {
                throw DiaryErrorCode.POST_ID_REQUIRED.defaultException();
            }
            // postId로 MemoryPost 조회 (게시글이 없으면 예외 발생)
            MemoryPost post = memoryPostRepository.findById(postId)
                    .orElseThrow(DiaryErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

            // post 의 내용으로 그림일기 AI 호출
            String content = post.getMemoryPostContent();
            DiaryContentDto aiGeneratedContent = callAiServer(content);

            LocalDate memoryDate = post.getMemoryDate();
            String weekday = memoryDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            return GetDiaryResponse.from(memoryDate, weekday, aiGeneratedContent);
        }

        public GetSavedDiaryResponse saveDiaryImage(Long userId, MultipartFile diaryImage) {
            if (diaryImage == null || diaryImage.isEmpty()) {
                throw DiaryErrorCode.IMAGE_FILE_EMPTY.defaultException();
            }

            Users user = usersRepository.findById(userId)
                    .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);
            Family family = user.getFamily();
            if (family == null) {
                throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
            }

            String diaryImageKey = s3Service.upload(diaryImage, UploadDirectory.DIARY, family.getId(), "family");

            DiaryBook diaryBook = DiaryBook.builder()
                    .family(family)
                    .diaryImage(diaryImageKey)
                    .build();
            diaryBookRepository.save(diaryBook);

            return GetSavedDiaryResponse.from(s3Service.generateUrlFromKey(diaryImageKey));

        }


        // 그림일기 호출 메서드
        private DiaryContentDto callAiServer(String content) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("user_text", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            try {
                // AI 서버로부터 List<DiaryContentDto> 타입의 응답을 직접 받기 위해 exchange 사용
                ResponseEntity<DiaryContentDto> response = restTemplate.postForEntity(
                        aiServerUrl + "/generate-diary",
                        entity,
                        DiaryContentDto.class
                );
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                } else {
                    log.error("AI 서버로부터 유효한 응답을 받지 못했습니다. status: {}", response.getStatusCode());
                    throw DiaryErrorCode.AI_SERVER_FAILED.defaultException();
                }

            } catch (RestClientException e) {
                log.error("AI 서버와 통신 중 오류가 발생했습니다.", e);
                throw DiaryErrorCode.AI_COMMUNICATION_ERROR.defaultException();
            }
        }


    }
