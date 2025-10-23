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
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cdn.domain}")
    private String cdnDomain;

    @Value("${app.proxy.base-url}")
    private String proxyHost;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "mp4");

    private static final Map<String, String> MIME_BY_EXT = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "mp4", "video/mp4"
            // 필요 시 추가
    );

    public String upload(MultipartFile file, UploadDirectory directory, Long id, String idLabel) {
        //1. 파일 유효성 검사하기
        validateImageFile(file);

        // 2. S3에 저장될 파일 경로 생성 (e.g., posts/1/uuid.jpg)
        String extension = getExtension(file);
        String key = generateKey(directory, id, idLabel, extension);
        String mime = guessMimeFromExt(extension);

        // 3. S3 업로드 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(mime)
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
        // 1) 프록시 언랩 → CDN URL 검증 → temp key 추출
        String sourceKey = extractTempKeyFromAnyUrl(tempUrl);

        // 2) 확장자/MIME 추출 후 목적지 키 생성
        String extension = sourceKey.substring(sourceKey.lastIndexOf(".") + 1);
        String mime = guessMimeFromExt(extension);

        // 예: videos/family/{id}/{uuid}.{ext}
        String destinationKey = generateKey(directory, id, idLabel, extension);

        log.info("S3 객체 이동 시작. Source: {} -> Destination: {}", sourceKey, destinationKey);
        try {
            // 3) Copy (필요시 암호화/메타데이터/CacheControl 지정)
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucket)
                    .destinationKey(destinationKey)
                    .metadataDirective(MetadataDirective.REPLACE)
                    .contentType(mime)
                    .build();
            s3Client.copyObject(copyReq);
            log.info("S3 객체 복사 성공.");

            // 4) 원본 삭제
            delete(sourceKey);
            log.info("원본 임시 S3 객체 삭제 성공.");

        } catch (SdkException e) {
            log.error("S3 객체 이동(복사 후 삭제) 실패. Source: {}", sourceKey, e);
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
        return "https://" + cdnDomain + "/" + key;
    }

    // key 값 추출 함수
    public Optional<String> extractKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            log.info("url 값이 null/blank 입니다.");
            return Optional.empty();
        }
        try {
            URI u = URI.create(url);
            // 쿼리 스트링 제거된 경로에서 앞의 '/' 제거
            String rawPath = u.getRawPath();               // 인코딩 유지된 경로
            if (rawPath == null || rawPath.length() <= 1) return Optional.empty();
            String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;

            String host = u.getHost() == null ? "" : u.getHost();

            // 1) CloudFront 도메인
            if (host.equalsIgnoreCase(cdnDomain)) {
                return Optional.of(URLDecoder.decode(path, StandardCharsets.UTF_8));
            }

            // 2) S3 REST 엔드포인트 (region/dualstack 포함 다양한 변형 허용)
            // 예: {bucket}.s3.ap-northeast-2.amazonaws.com / {bucket}.s3.dualstack.ap-northeast-2.amazonaws.com
            if (host.toLowerCase().startsWith((bucket + ".s3").toLowerCase())) {
                return Optional.of(URLDecoder.decode(path, StandardCharsets.UTF_8));
            }

            log.warn("알 수 없는 호스트로부터 온 URL: host={}, url={}", host, url);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.warn("URL 파싱 실패: {}", url, e);
            return Optional.empty();
        }
    }



    private void validateImageFile(MultipartFile file) {
        // 파일이 비어있을 때
        if (file.isEmpty()) {
            throw S3ErrorCode.EMPTY_FILE.defaultException();
        }
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


    private String guessMimeFromExt(String ext) {
        return MIME_BY_EXT.getOrDefault(ext.toLowerCase(), "application/octet-stream");
    }

    // 프록시/직접 URL을 모두 받아 temp key를 뽑아낸다
    private String extractTempKeyFromAnyUrl(String tempUrl) {
        String unwrapped = unwrapIfProxyUrl(tempUrl);
        return extractTempKeyFromCdnUrl(unwrapped);
    }

    private String unwrapIfProxyUrl(String proxiedUrl) {
        // 예) https://api.../proxy/fetch?url=https%3A%2F%2Fcdn...%2Ftemp%2Fvideos%2Fxxx.mp4
        URI uri = URI.create(proxiedUrl);

        // (선택) proxyHost가 설정되어 있으면 호스트 확인
        if (proxyHost != null && !proxyHost.isBlank() && proxyHost.equalsIgnoreCase(uri.getHost())) {
            // 쿼리에서 url 파라미터 파싱
            String query = uri.getRawQuery();
            if (query == null)  {
                throw S3ErrorCode.INVALID_S3_URL.defaultException();
            }
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2 && kv[0].equals("url")) {
                    return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
                }
            }
            throw S3ErrorCode.INVALID_S3_URL.defaultException();
        }

        // 프록시가 아니면 그대로 반환(이미 CDN URL)
        return proxiedUrl;
    }

    private String extractTempKeyFromCdnUrl(String cdnUrl) {
        // 허용된 CDN 도메인만 수락
        URI uri = URI.create(cdnUrl);
        String host = uri.getHost();
        if (host == null || !host.equalsIgnoreCase(cdnDomain)) {
            throw S3ErrorCode.INVALID_S3_URL.defaultException();
        }

        // 허용된 프리픽스만 수락 (임시 비디오 전용)
        String path = uri.getPath(); // /temp/videos/xxx.mp4
        if (path == null || !path.startsWith("/temp/videos/")) {
            throw S3ErrorCode.INVALID_S3_URL.defaultException();
        }

        // 선행 슬래시 제거하여 S3 Key로 변환
        return path.startsWith("/") ? path.substring(1) : path;
    }

}
