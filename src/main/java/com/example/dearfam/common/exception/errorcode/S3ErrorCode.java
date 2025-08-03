package com.example.dearfam.common.exception.errorcode;

import com.example.dearfam.common.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode{

    EMPTY_FILE("파일이 비어 있습니다", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_MIME("이미지 MIME 타입이 아닙니다", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNSUPPORTED_EXTENSION("지원하지 않는 확장자입니다", HttpStatus.BAD_REQUEST),
    MISSING_EXTENSION("확장자가 없습니다", HttpStatus.BAD_REQUEST),
    INVALID_S3_URL("유효하지 않은 S3 URL입니다", HttpStatus.BAD_REQUEST),
    IMAGE_LIMIT_EXCEEDED("이미지는 최대 10개까지 업로드 가능합니다", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED("S3 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_FAILED("S3 이미지 삭제에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    OBJECT_COPY_FAILED("S3 객체 복사에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public HttpStatus defaultHttpStatus() {
        return httpStatus;
    }

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public S3Exception defaultException() {
        return new S3Exception(this);
    }

    @Override
    public S3Exception defaultException(Throwable cause) {
        return new S3Exception(this, cause);
    }
}
