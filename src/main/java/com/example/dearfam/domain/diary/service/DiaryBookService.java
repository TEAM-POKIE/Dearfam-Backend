    package com.example.dearfam.domain.diary.service;

    import com.example.dearfam.domain.diary.controller.request.DiaryGenerateRequest;
    import com.example.dearfam.domain.diary.controller.response.GetDiaryResponse;
    import com.example.dearfam.domain.diary.dto.DiaryContentDto;
    import com.example.dearfam.domain.diary.exception.DiaryErrorCode;
    import com.example.dearfam.domain.diary.exception.DiaryException;
    import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
    import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.*;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClientException;
    import org.springframework.web.client.RestTemplate;

    import java.time.LocalDate;
    import java.time.format.TextStyle;
    import java.util.*;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class DiaryBookService {

        private final RestTemplate restTemplate;
        private final MemoryPostRepository memoryPostRepository;

        @Value("${ai.server.url}")
        private String aiServerUrl;

        public GetDiaryResponse generateDiaryBook(DiaryGenerateRequest request) {
            // postId 리스트 가져오기
            Long postId = request.getPostId();
            if (postId == null) {
                throw DiaryErrorCode.POST_ID_REQUIRED.defaultException();
            }

            // 2. postId로 MemoryPost 조회 (게시글이 없으면 예외 발생)
            MemoryPost post = memoryPostRepository.findById(postId)
                    .orElseThrow(DiaryErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

            String content = post.getMemoryPostContent();

            DiaryContentDto aiGeneratedContent = callAiServer(content);

            LocalDate memoryDate = post.getMemoryDate();
            String weekday = memoryDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            return GetDiaryResponse.from(memoryDate, weekday, aiGeneratedContent);
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
