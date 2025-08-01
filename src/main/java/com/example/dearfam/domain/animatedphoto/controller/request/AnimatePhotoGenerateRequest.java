package com.example.dearfam.domain.animatedphoto.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnimatePhotoGenerateRequest {

    @NotBlank(message = "actionPrompt는 비어 있을 수 없습니다.")
    private String actionPrompt;

}
