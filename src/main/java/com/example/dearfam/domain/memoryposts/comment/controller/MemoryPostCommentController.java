package com.example.dearfam.domain.memoryposts.comment.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.memoryposts.comment.controller.request.CreateCommentRequest;
import com.example.dearfam.domain.memoryposts.comment.controller.response.GetAllCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.controller.response.GetCreatedCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.dto.MemoryPostCommentDto;
import com.example.dearfam.domain.memoryposts.comment.service.MemoryPostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/memory-post/{postId}/comment")
@Tag(name = "Memory Post Comment Controller", description = "추억 게시글 댓글 관련 기능을 처리하는 API")
public class MemoryPostCommentController {

    private final JwtService jwtService;
    private final MemoryPostCommentService memoryPostCommentService;

    @Operation(
            summary = "댓글 생성",
            description = "게시글의 댓글을 생성하고, DB에 저장하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "작성자, 게시글 정보를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping
    public Response<GetCreatedCommentResponse> createMemoryPostComment(@Valid @RequestBody CreateCommentRequest request, @PathVariable Long postId) {
        Long writerId = jwtService.getTokenDto().getUserId();
        String content = request.getContent();

        MemoryPostCommentDto memoryPostCommentDto = memoryPostCommentService.createMemoryPostComment(writerId, postId, content);

        GetCreatedCommentResponse response = GetCreatedCommentResponse.from(memoryPostCommentDto);

        return Response.data(response);
    }

    @Operation(
            summary = "댓글 삭제",
            description = "작성자가 자신이 작성한 댓글을 삭제하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "삭제 권한이 없는 사용자"),
                    @ApiResponse(responseCode = "404", description = "게시글, 댓글 정보를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @DeleteMapping("/{commentId}")
    public Response<String> deleteMemoryPostComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Long writerId = jwtService.getTokenDto().getUserId();

        memoryPostCommentService.deleteMemoryPostComment(writerId, postId, commentId);

        return Response.data("댓글이 삭제되었습니다.");
    }

    @Operation(
            summary = "게시글 댓글 조회",
            description = "한 게시글의 모든 댓글을 생성된 순서대로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "게시글 정보를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping
    public Response<List<GetAllCommentResponse>> getAllCommentsFromMemoryPost(@PathVariable Long postId) {

        List<MemoryPostCommentDto> memoryPostCommentDtoList = memoryPostCommentService.getCommentsFromMemoryPost(postId);

        List<GetAllCommentResponse> response = GetAllCommentResponse.from(memoryPostCommentDtoList);

        return Response.data(response);
    }

}
