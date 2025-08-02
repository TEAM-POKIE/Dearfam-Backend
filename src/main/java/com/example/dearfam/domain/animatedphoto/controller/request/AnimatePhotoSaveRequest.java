package com.example.dearfam.domain.animatedphoto.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnimatePhotoSaveRequest {

    @NotBlank(message = "저장 시 임시 주소를 보내야 합니다.")
    @URL(message = "유효한 URL 형식이 아닙니다.")
    private String tempVideoUrl;
}
