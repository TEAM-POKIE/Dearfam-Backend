package com.example.dearfam.common.exception.errorcode;

import com.example.dearfam.common.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode{

    EMPTY_FILE("파일이 비어 있습니다", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_MIME("이미지 MIME 타입이 아닙니다", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNSUPPORTED_EXTENSION("지원하지 않는 확장자입니다", HttpStatus.BAD_REQUEST),
    MISSING_EXTENSION("확장자가 없습니다", HttpStatus.BAD_REQUEST);

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
