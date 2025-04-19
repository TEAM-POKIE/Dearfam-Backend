package com.example.dearfam.domain.memoryposts.like.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.memoryposts.like.service.MemoryPostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memory-post")
@Tag(name = "Memory Post Like Controller", description = "추억 게시글 좋아요 처리 API")
public class MemoryPostLikeController {

    private final JwtService jwtService;
    private final MemoryPostLikeService memoryPostLikeService;

    @Operation(
            summary = "게시글 좋아요",
            description = "사용자가 요청한 게시글 좋아요 API. 한 번 더 누르면 좋아요 취소임",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PutMapping("/{postId}/like")
    public Response<String> memoryPostLikeOrUnlike(@PathVariable Long postId) {
        Long likedUserId = jwtService.getTokenDto().getUserId();

        String response = memoryPostLikeService.likeOrUnlikeMemoryPost(likedUserId, postId);

        return Response.data(response);
    }
}
