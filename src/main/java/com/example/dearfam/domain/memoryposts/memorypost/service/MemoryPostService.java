package com.example.dearfam.domain.memoryposts.memorypost.service;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.memoryposts.members.repository.MemoryPostFamilyMembersRepository;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostFamilyMembersResponse;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetMemoryPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.GetUpdatedPostResponse;
import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.memoryposts.memorypost.exception.MemoryPostErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
import com.example.dearfam.domain.users.dto.FamilyMemberDto;
import com.example.dearfam.domain.users.entity.UserFamilyRole;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryPostService {
    private final UsersRepository usersRepository;
    private final MemoryPostRepository memoryPostRepository;
    private final MemoryPostFamilyMembersRepository memoryPostFamilyMembersRepository;

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

        if (participantFamilyMemberIds != null && !participantFamilyMemberIds.isEmpty()) {
            // 현재 가족 구성원 조회
            List<Users> familyMembers = usersRepository.findAllByFamilyId(family.getId());

            // 가족 구성원을 Map으로 <id, id에 대한 Users 객체> 로 구성
            Map<Long, Users> familyMemberMap = familyMembers.stream()
                    .collect(Collectors.toMap(Users::getId, Function.identity()));

            // 참여 가족 id를 통해 현재 가족 구성원에서 참여한 가족의 객체를 맵핑하고, MemoryPostFamilyMembers 객체를 빌드한다.
            List<MemoryPostFamilyMembers> memoryPostFamilyMembers = participantFamilyMemberIds.stream()
                    .map(id -> {
                        Users user = familyMemberMap.get(id);
                        if (user == null) {
                            throw UsersErrorCode.FAMILY_MEMBER_NOT_FOUND.defaultException();
                        }
                        return MemoryPostFamilyMembers.builder()
                                .joinedFamilyMember(user)
                                .memoryPost(memoryPost)
                                .build();
                    })
                    .toList();

            memoryPostFamilyMembersRepository.saveAll(memoryPostFamilyMembers);
        }

        // TODO : 추후 이미지 저장 로직 여기서 추가 구현

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

    @Transactional(readOnly = true)
    public GetMemoryPostFamilyMembersResponse getMemoryPostFamilyMembers(Long postId) {
        MemoryPost post = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        List<MemoryPostFamilyMembers> relations = memoryPostFamilyMembersRepository.findAllByMemoryPost(post);

        List<FamilyMemberDto> participants = relations.stream()
                .map(MemoryPostFamilyMembers::getJoinedFamilyMember)
                .sorted(Comparator
                        .comparing((Users u) -> {
                            UserFamilyRole userFamilyRole = u.getUserFamilyRole();
                            return userFamilyRole != null ? userFamilyRole.getSortOrder() : Integer.MAX_VALUE;
                        })
                        .thenComparing(BaseTimeEntity::getCreatedAt))
                .map(FamilyMemberDto::from)
                .toList();


        return GetMemoryPostFamilyMembersResponse.from(postId, participants);
    }

}
