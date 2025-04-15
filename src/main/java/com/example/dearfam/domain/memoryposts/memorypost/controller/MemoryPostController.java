package com.example.dearfam.domain.memoryposts.memorypost.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.memoryposts.memorypost.controller.request.CreateMemoryPostRequest;
import com.example.dearfam.domain.memoryposts.memorypost.controller.request.UpdateMemoryPostRequest;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostFamilyMembersResponse;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetUpdatedPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.service.MemoryPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/memory-post")
@Tag(name = "Memory Posts Controller", description = "추억 게시글 관련 기능을 처리하는 API")
public class MemoryPostController {
    private final MemoryPostService memoryPostService;
    private final JwtService jwtService;

    @Operation(
            summary = "추억 게시글 생성",
            description = "현재 로그인한 유저가 작성한 추억 게시글을 생성합니다."
    )
    @PostMapping
    public Response<GetMemoryPostResponse> createMemoryPost(@Valid @RequestBody CreateMemoryPostRequest createMemoryPostRequest) {
        // 로그인한 유저가 게시글을 올리면, writer 는 현재 로그인한 유저임.
        Long writerId = jwtService.getTokenDto().getUserId();
        String title = createMemoryPostRequest.getTitle();
        String content = createMemoryPostRequest.getContent();
        LocalDate memoryDate = createMemoryPostRequest.getMemoryDate();
        List<Long> participantFamilyMemberIds = createMemoryPostRequest.getParticipantFamilyMemberIds();

        GetMemoryPostResponse response = memoryPostService.createMemoryPost(
                writerId,
                title,
                content,
                memoryDate,
                participantFamilyMemberIds
                );

        return Response.data(response);
    }

    @Operation(
            summary = "추억 게시글 수정",
            description = "추억 게시글을 수정합니다. 수정 가능사항 : 제목, 내용",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "MEMORY_POST_NOT_FOUND"),
                    @ApiResponse(responseCode = "403", description = "수정 권한이 없음."),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR"),
            }
    )
    @PutMapping("/{postId}")
    public Response<GetUpdatedPostResponse> updateMemoryPost(@PathVariable Long postId,
                                                             @Valid @RequestBody UpdateMemoryPostRequest updateMemoryPostRequest) {
        Long writerId = jwtService.getTokenDto().getUserId();
        String title = updateMemoryPostRequest.getTitle();
        String content = updateMemoryPostRequest.getContent();

        GetUpdatedPostResponse response = memoryPostService.updateMemoryPost(writerId, postId, title, content);

        return Response.data(response);
    }

    @Operation(
            summary = "추억 게시글 삭제",
            description = "추억 게시글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "MEMORY_POST_NOT_FOUND"),
                    @ApiResponse(responseCode = "403", description = "삭제 권한이 없음."),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR"),
            }
    )
    @DeleteMapping("/{postId}")
    public Response<String> deleteMemoryPost(@PathVariable Long postId) {
        Long writerId = jwtService.getTokenDto().getUserId();

        memoryPostService.deleteMemoryPost(writerId, postId);

        return Response.data("게시글을 삭제하였습니다.");
    }

    @Operation(
            summary = "게시글 참여 가족 리스트 조회",
            description = "게시글 ID를 통해 추억 게시글에 참여한 가족들의 ㅁ"
    )
    @GetMapping("/{postId}/family-members")
    public Response<GetMemoryPostFamilyMembersResponse> getMemoryPostFamilyMembers(@PathVariable Long postId) {

        GetMemoryPostFamilyMembersResponse response = memoryPostService.getMemoryPostFamilyMembers(postId);

        return Response.data(response);
    }

}
