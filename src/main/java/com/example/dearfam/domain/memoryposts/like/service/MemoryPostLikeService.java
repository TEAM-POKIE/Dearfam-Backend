package com.example.dearfam.domain.memoryposts.like.service;

import com.example.dearfam.domain.memoryposts.like.entity.MemoryPostLike;
import com.example.dearfam.domain.memoryposts.like.repository.MemoryPostLikeRepository;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.memoryposts.memorypost.exception.MemoryPostErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoryPostLikeService {

    private final UsersRepository usersRepository;
    private final MemoryPostRepository memoryPostRepository;
    private final MemoryPostLikeRepository memoryPostLikeRepository;

    @Transactional
    public String likeOrUnlikeMemoryPost(Long likedUserId, Long postId) {

        Users likedUser = usersRepository.findById(likedUserId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        MemoryPostLike like = memoryPostLikeRepository.findByLikedUserAndMemoryPost(likedUser, memoryPost);

        if (like == null) {
            like = MemoryPostLike.builder()
                    .likedUser(likedUser)
                    .memoryPost(memoryPost)
                    .liked(true)
                    .build();
            memoryPostLikeRepository.save(like);
            return "좋아요를 눌렀습니다.";
        }

        if (like.isLiked()) {
            like.setLiked(false);
            return "좋아요를 취소했습니다.";
        } else {
            like.setLiked(true);
            return "좋아요를 다시 눌렀습니다.";
        }

    }

}
