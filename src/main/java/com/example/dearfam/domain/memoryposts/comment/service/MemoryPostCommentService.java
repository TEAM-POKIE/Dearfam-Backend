package com.example.dearfam.domain.memoryposts.comment.service;

import com.example.dearfam.domain.memoryposts.comment.controller.response.GetAllCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.controller.response.GetCreatedCommentResponse;
import com.example.dearfam.domain.memoryposts.comment.entity.MemoryPostComment;
import com.example.dearfam.domain.memoryposts.comment.exception.MemoryPostCommentErrorCode;
import com.example.dearfam.domain.memoryposts.comment.mapper.MemoryPostCommentMapper;
import com.example.dearfam.domain.memoryposts.comment.repository.MemoryPostCommentRepository;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.memoryposts.memorypost.exception.MemoryPostErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemoryPostCommentService {

    private final UsersRepository usersRepository;
    private final MemoryPostRepository memoryPostRepository;
    private final MemoryPostCommentRepository memoryPostCommentRepository;
    private final MemoryPostCommentMapper memoryPostCommentMapper;

    @Transactional
    public GetCreatedCommentResponse createMemoryPostComment(Long writerId, Long postId, String commentContent) {

        Users writer = usersRepository.findById(writerId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        // 같은 가족의 게시글인지를 검사
        if (writer.getFamily() == null || !Objects.equals(writer.getFamily().getId(), memoryPost.getFamily().getId())) {
            throw MemoryPostErrorCode.UNAUTHORIZED_FAMILY_ACCESS.defaultException();
        }

        MemoryPostComment memoryPostComment = MemoryPostComment.builder()
                .memoryPost(memoryPost)
                .commentWriter(writer)
                .commentContent(commentContent)
                .build();

        memoryPostCommentRepository.save(memoryPostComment);

        memoryPost.setMemoryPostCommentCount(memoryPost.getMemoryPostCommentCount() + 1);

        return memoryPostCommentMapper.toGetCreatedCommentResponse(memoryPostComment);
    }

    @Transactional
    public void deleteMemoryPostComment(Long writerId, Long postId, Long commentId) {

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        MemoryPostComment comment = memoryPostCommentRepository.findById(commentId)
                .orElseThrow(MemoryPostCommentErrorCode.COMMENT_NOT_FOUND::defaultException);

        // 삭제 권한이 있는지 확인
        if (!comment.getCommentWriter().getId().equals(writerId)) {
            throw MemoryPostCommentErrorCode.UNAUTHORIZED_WRITER.defaultException();
        }

        memoryPost.setMemoryPostCommentCount(memoryPost.getMemoryPostCommentCount() - 1);
        memoryPostCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<GetAllCommentResponse> getCommentsFromMemoryPost(Long postId) {

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        List<MemoryPostComment> comments = memoryPostCommentRepository
                .findAllByMemoryPostOrderByCreatedAtAsc(memoryPost);

        return comments.stream()
                .map(memoryPostCommentMapper::toGetAllCommentResponse)
                .toList();
    }

}
