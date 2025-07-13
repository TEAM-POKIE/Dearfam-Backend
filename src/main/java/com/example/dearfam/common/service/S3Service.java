package com.example.dearfam.common.service;

import com.example.dearfam.common.exception.errorcode.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

        //1. 파일 유효성 검사하기
        validateImageFile(file);

        // 2. S3에 저장될 파일 경로 생성 (e.g., posts/1/uuid.jpg)
        String extension = getExtension(file);

        // 3. S3 업로드 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            log.error("파일을 읽어오는 중 오류가 발생했습니다.", e);
            throw S3ErrorCode.UPLOAD_FAILED.defaultException(e);
        } catch (SdkException e) {
            log.error("S3 업로드에 실패했습니다.", e);
            throw S3ErrorCode.UPLOAD_FAILED.defaultException(e);
        }

        return key;
    }

    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 이미지 삭제 성공 - key: {}", key);
        } catch (SdkException e) {
            log.error("S3 이미지 삭제 실패 - key: {}", key, e);
            throw S3ErrorCode.DELETE_FAILED.defaultException(e);
        }
    }

    public String generateUrlFromKey(String key) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.utilities().getUrl(getUrlRequest).toExternalForm();
    }

    // key 값 추출 함수
    public Optional<String> extractKeyFromUrl(String url) {
        if (url == null) {
            log.info("url 값이 null 입니다.");
            return Optional.empty();
        }

        String s3UrlPrefix = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";

        if (!url.startsWith(s3UrlPrefix)) {
            log.warn("s3 url 형식이 아닙니다.");
            return Optional.empty();
        }
        return Optional.of(url.substring(s3UrlPrefix.length()));
    }

    private void validateImageFile(MultipartFile file) {
        // 파일이 비어있을 때
        if (file.isEmpty()) {
            throw S3ErrorCode.EMPTY_FILE.defaultException();
        }
        // TODO: 파일이 image 검사 X multipart/form-data 인지를 검사
        // 파일이 이미지가 아닐때 (Content Type이 image로 시작하지 않는 경우)
//        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
//            log.error(file.getContentType());
//            throw S3ErrorCode.INVALID_IMAGE_MIME.defaultException();
//        }
        // 지원하지 않는 확장자인 경우
        if (!ALLOWED_EXTENSIONS.contains(getExtension(file))) {
            throw S3ErrorCode.UNSUPPORTED_EXTENSION.defaultException();
        }
    }

    // 파일 확장자 추출하는 함수
    private String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !Objects.requireNonNull(fileName).contains(".")) {
            throw S3ErrorCode.MISSING_EXTENSION.defaultException();
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    }

}
