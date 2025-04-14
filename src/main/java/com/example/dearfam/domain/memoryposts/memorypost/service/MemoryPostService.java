package com.example.dearfam.domain.memoryposts.memorypost.service;

import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetUpdatedPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.memoryposts.memorypost.exception.MemoryPostErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryPostService {
    private final UsersRepository usersRepository;
    private final MemoryPostRepository memoryPostRepository;

    @Transactional
    public GetMemoryPostResponse createMemoryPost(Long writerId, String title, String content,
                                                  LocalDate memoryDate, List<Long> participantFamilyMemberIds) {

        Users writer = usersRepository.findById(writerId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        Family family = writer.getFamily();
        if (family == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        MemoryPost memoryPost = MemoryPost.builder()
                .writer(writer)
                .family(family)
                .memoryPostTitle(title)
                .memoryPostContent(content)
                .memoryDate(memoryDate)
                .build();

        memoryPostRepository.save(memoryPost);

        // TODO : 추후 참여자, 이미지 저장 로직 여기서 추가 구현

        MemoryPostDto memoryPostDto = MemoryPostDto.from(memoryPost);

        return GetMemoryPostResponse.from(memoryPostDto);
    }

    @Transactional
    public GetUpdatedPostResponse updateMemoryPost(Long writerId, Long postId, String title, String content) {

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        if (!memoryPost.getWriter().getId().equals(writerId)) {
            throw MemoryPostErrorCode.MEMORY_POST_NOT_FOUND.defaultException();
        }

        memoryPost.setMemoryPostTitle(title);
        memoryPost.setMemoryPostContent(content);
        memoryPostRepository.save(memoryPost);

        return GetUpdatedPostResponse.from(memoryPost.getId(),
                memoryPost.getMemoryPostTitle(),
                memoryPost.getMemoryPostContent()
        );
    }

    @Transactional
    public void deleteMemoryPost(Long writerId, Long postId) {
        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        if (!memoryPost.getWriter().getId().equals(writerId)) {
            throw MemoryPostErrorCode.MEMORY_POST_NOT_FOUND.defaultException();
        }
        memoryPostRepository.delete(memoryPost);
    }

}
