package com.example.dearfam.common.service;

import com.example.dearfam.common.entity.UploadDirectory;
import com.example.dearfam.common.exception.errorcode.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    public String upload(MultipartFile file, UploadDirectory directory, Long id, String idLabel) {
        //1. 파일 유효성 검사하기
        validateImageFile(file);

        // 2. S3에 저장될 파일 경로 생성 (e.g., posts/1/uuid.jpg)
        String extension = getExtension(file);
        String key = generateKey(directory, id, idLabel, extension);

        // 3. S3 업로드 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("파일 업로드 성공 - dir: {}, id: {}, key: {}", directory, id, key);
        } catch (IOException e) {
            log.error("파일을 읽어오는 중 오류가 발생했습니다.", e);
            throw S3ErrorCode.UPLOAD_FAILED.defaultException(e);
        } catch (SdkException e) {
            log.error("S3 업로드에 실패했습니다.", e);
            throw S3ErrorCode.UPLOAD_FAILED.defaultException(e);
        }

        return key;
    }

    public String moveTempFileToPermanentLocation(String tempUrl, UploadDirectory directory, Long id, String idLabel) {
        // 1. 임시 URL에서 원본 Key 추출
        String sourceKey = extractKeyFromUrl(tempUrl)
                .orElseThrow(S3ErrorCode.INVALID_S3_URL::defaultException);

        // 2. 원본 Key에서 확장자 추출 후, 영구 저장될 새로운 Key 생성
        String extension = sourceKey.substring(sourceKey.lastIndexOf(".") + 1);
        String destinationKey = generateKey(directory, id, idLabel, extension);

        log.info("S3 객체 이동 시작. Source: {} -> Destination: {}", sourceKey, destinationKey);
        try {
            // 3. 객체 복사
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucket)
                    .destinationKey(destinationKey)
                    .build();
            s3Client.copyObject(copyReq);
            log.info("S3 객체 복사 성공.");

            // 4. 원본 객체 삭제
            delete(sourceKey);
            log.info("원본 임시 S3 객체 삭제 성공.");

        } catch (SdkException e) {
            log.error("S3 객체 이동(복사 후 삭제) 실패. Source: {}", sourceKey, e);
            // S3ErrorCode에 OBJECT_MOVE_FAILED 와 같은 에러 코드를 추가하여 사용하는 것을 권장합니다.
            throw S3ErrorCode.OBJECT_COPY_FAILED.defaultException(e);
        }

        return destinationKey;
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

    public void deleteAllByKeys(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            log.info("삭제할 S3 키가 없습니다.");
            return;
        }

        List<ObjectIdentifier> toDelete = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        try {
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
            log.info("S3 객체 {}개 일괄 삭제 성공.", keys.size());
        } catch (SdkException e) {
            log.error("S3 객체 일괄 삭제 실패.", e);
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

    private String generateKey(UploadDirectory directory, Long id, String idLabel, String extension) {
        String prefix = idLabel + "-" + id;
        return directory.getBaseDir() + "/" + prefix + "/" + UUID.randomUUID() + "." + extension;
    }

}
