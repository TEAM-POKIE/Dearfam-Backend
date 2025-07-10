package com.example.dearfam.domain.memoryposts.image.dto;

import com.example.dearfam.domain.memoryposts.image.entity.MemoryPostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemoryPostImageDto {

    private final String imageUrl;
    private final int imageOrder;

    public static MemoryPostImageDto from(MemoryPostImage memoryPostImage, String imageUrl) {
        return MemoryPostImageDto.builder()
                .imageUrl(imageUrl)
                .imageOrder(memoryPostImage.getImageOrder())
                .build();
    }

}
