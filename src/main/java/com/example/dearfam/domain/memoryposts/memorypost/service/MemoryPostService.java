package com.example.dearfam.domain.memoryposts.memorypost.service;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.common.entity.UploadDirectory;
import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.memoryposts.image.dto.MemoryPostImageDto;
import com.example.dearfam.domain.memoryposts.image.entity.MemoryPostImage;
import com.example.dearfam.domain.memoryposts.image.repository.MemoryPostImageRepository;
import com.example.dearfam.domain.memoryposts.like.repository.MemoryPostLikeRepository;
import com.example.dearfam.domain.memoryposts.members.dto.MemoryPostFamilyMembersDto;
import com.example.dearfam.domain.memoryposts.members.entity.MemoryPostFamilyMembers;
import com.example.dearfam.domain.memoryposts.members.repository.MemoryPostFamilyMembersRepository;
import com.example.dearfam.domain.memoryposts.memorypost.controller.request.CreateMemoryPostRequest;
import com.example.dearfam.domain.memoryposts.memorypost.controller.response.*;
import com.example.dearfam.domain.memoryposts.memorypost.dto.MemoryPostDto;
import com.example.dearfam.domain.memoryposts.memorypost.dto.SimpleMemoryPostDto;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.memoryposts.memorypost.exception.MemoryPostErrorCode;
import com.example.dearfam.domain.memoryposts.memorypost.repository.MemoryPostRepository;
import com.example.dearfam.domain.users.dto.FamilyMemberDto;
import com.example.dearfam.domain.users.entity.UserFamilyRole;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.mapper.UsersMapper;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryPostService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final MemoryPostRepository memoryPostRepository;
    private final MemoryPostFamilyMembersRepository memoryPostFamilyMembersRepository;
    private final MemoryPostLikeRepository memoryPostLikeRepository;
    private final S3Service s3Service;
    private final MemoryPostImageRepository memoryPostImageRepository;

    @Transactional
    public GetMemoryPostResponse createMemoryPost(Long writerId, CreateMemoryPostRequest requestDto, List<MultipartFile> images) {

        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        LocalDate memoryDate = requestDto.getMemoryDate();
        List<Long> participantFamilyMemberIds = requestDto.getParticipantFamilyMemberIds();

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
        // 생성한 게시글 저장
        memoryPostRepository.save(memoryPost);
        log.info("게시글 생성 완료");


        List<MemoryPostImageDto> imageDtos = new ArrayList<>();
        // 이미지 S3에 저장 후 DB에 URL 과 순서 저장
        if (images != null && !images.isEmpty()) {
        boolean hasValidImages = images != null &&
                images.stream().anyMatch(file -> file != null && !file.isEmpty());
        if (hasValidImages) {
            log.info("이미지 null 값 아님");
            for (int i = 0; i < images.size(); i++) {
                String imageKey = s3Service.uploadPostImages(images.get(i), memoryPost.getId());
                String imageKey = s3Service.upload(images.get(i), UploadDirectory.POSTS,memoryPost.getId());
                String imageUrl = s3Service.generateUrlFromKey(imageKey);
                MemoryPostImage image = MemoryPostImage.builder()
                        .imageKey(imageKey)
                        .imageOrder(i + 1)
                        .build();

                memoryPost.addImage(image); // 연관관계 양방향 설정
                imageDtos.add(MemoryPostImageDto.from(image, imageUrl));
            }
            memoryPost.setMemoryPostImageCount(memoryPost.getMemoryPostImages().size());
            memoryPostRepository.save(memoryPost); // Cascade 설정돼 있으면 이미지도 저장됨
            log.info("이미지 저장 완료");
        }

        // 게시글에 참여한 가족 구성원 저장
        List<MemoryPostFamilyMembers> memoryPostFamilyMembers = List.of();
        if (participantFamilyMemberIds != null && !participantFamilyMemberIds.isEmpty()) {
            // 현재 가족 구성원 조회
            List<Users> familyMembers = usersRepository.findAllByFamilyId(family.getId());

            // 가족 구성원을 Map으로 <id, id에 대한 Users 객체> 로 구성
            Map<Long, Users> familyMemberMap = familyMembers.stream()
                    .collect(Collectors.toMap(Users::getId, Function.identity()));

            // 참여 가족 id를 통해 현재 가족 구성원에서 참여한 가족의 객체를 맵핑하고, MemoryPostFamilyMembers 객체를 빌드한다.
            memoryPostFamilyMembers = participantFamilyMemberIds.stream()
                    .filter(id -> !id.equals(writerId)) // 작성자는 제외
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

        MemoryPostDto memoryPostDto = MemoryPostDto.from(memoryPost);
        List<MemoryPostFamilyMembersDto> membersDtos = MemoryPostFamilyMembersDto.from(memoryPostFamilyMembers);
        List<MemoryPostFamilyMembersDto> membersDtos = memoryPostFamilyMembers.stream()
                .map(usersMapper::toMemoryPostFamilyMembersDto)
                .toList();

        boolean isLiked = memoryPostLikeRepository.existsByLikedUserAndMemoryPost(writer, memoryPost);

        return GetMemoryPostResponse.from(memoryPostDto, membersDtos, imageDtos, isLiked);
    }

    @Transactional
    public GetUpdatedPostResponse updateMemoryPost(Long writerId, Long postId, String title, String content) {

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        if (!memoryPost.getWriter().getId().equals(writerId)) {
            throw MemoryPostErrorCode.UNAUTHORIZED_MEMORY_POST_ACCESS.defaultException();
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
            throw MemoryPostErrorCode.UNAUTHORIZED_MEMORY_POST_ACCESS.defaultException();
        }

        List<MemoryPostImage> images = memoryPost.getMemoryPostImages();
        for (MemoryPostImage img: images) {
            String key = img.getImageKey();
            if (key != null) {
                log.info("이미지 삭제 요청");
                s3Service.delete(key);
            } else {
                log.warn("이미지의 key 값이 null입니다. postId={}, imageId={}", memoryPost.getId(), img.getId());
            }
        }

        memoryPostRepository.delete(memoryPost);
        log.info("게시글 삭제 완료");
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
                .map(usersMapper::toFamilyMemberDto)
                .toList();


        return GetMemoryPostFamilyMembersResponse.from(post, participants);
    }

    @Transactional(readOnly = true)
    public GetMemoryPostResponse getMemoryPostById(Long userId, Long postId) {

        MemoryPost memoryPost = memoryPostRepository.findById(postId)
                .orElseThrow(MemoryPostErrorCode.MEMORY_POST_NOT_FOUND::defaultException);

        MemoryPostDto memoryPostDto = MemoryPostDto.from(memoryPost);

        List<MemoryPostFamilyMembers> familyMembers = memoryPostFamilyMembersRepository.findAllByMemoryPost(memoryPost);
        List<MemoryPostFamilyMembersDto> participants = MemoryPostFamilyMembersDto.from(familyMembers);
        List<MemoryPostFamilyMembersDto> participants = familyMembers.stream()
                .map(usersMapper::toMemoryPostFamilyMembersDto)
                .toList();

        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        // 로그인한 사용자의 가족이 아닌 경우에는 접근 불가
        if (!memoryPost.getFamily().getId().equals(user.getFamily().getId())) {
            throw MemoryPostErrorCode.UNAUTHORIZED_FAMILY_ACCESS.defaultException();
        }

        List<MemoryPostImageDto> imageDtos = memoryPost.getMemoryPostImages().stream()
                .map(img -> MemoryPostImageDto.from(img, s3Service.generateUrlFromKey(img.getImageKey())))
                .toList();

        boolean isLiked = memoryPostLikeRepository.existsByLikedUserAndMemoryPost(user, memoryPost);

        return GetMemoryPostResponse.from(memoryPostDto, participants, imageDtos, isLiked);
    }


    @Transactional(readOnly = true)
    public List<GetAllMemoryPostsResponse> getAllMemoryPostsByTimeOrder(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        Family family = user.getFamily();
        if (family == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        List<MemoryPost> memoryPosts = memoryPostRepository.findAllByFamilyOrderByMemoryDateDesc(family);
        List<SimpleMemoryPostDto> posts = SimpleMemoryPostDto.from(memoryPosts);
        List<SimpleMemoryPostDto> posts = memoryPosts.stream()
                .map(post -> {
                    String imageKey = post.getMemoryPostImages().stream()
                            .filter(img -> img.getImageOrder() == 1)
                            .map(MemoryPostImage::getImageKey)
                            .findFirst()
                            .orElse(null);

                    String imageUrl = imageKey != null ? s3Service.generateUrlFromKey(imageKey) : null;

                    return SimpleMemoryPostDto.from(post, imageUrl);
                })
                .toList();

        Map<Integer, List<SimpleMemoryPostDto>> postsGroupedByYear = new TreeMap<>(Comparator.reverseOrder());

        for (SimpleMemoryPostDto memoryPost : posts) {
            if (memoryPost.getMemoryDate() == null) continue;
            int year = memoryPost.getMemoryDate().getYear();
            postsGroupedByYear.computeIfAbsent(year, k -> new ArrayList<>()).add(memoryPost);
        }

        return GetAllMemoryPostsResponse.from(postsGroupedByYear);
    }

    @Transactional(readOnly = true)
    public List<GetRecentMemoryPostResponse> getRecentMemoryPosts(Long userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        Family family = user.getFamily();
        if (family == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        // memoryDate 기준 최근 10개의 데이터를 가져옴
        List<MemoryPost> memoryPosts  = memoryPostRepository.findTop10ByFamilyOrderByMemoryDateDesc(family);
        List<MemoryPostDto> memoryPostDtoList = MemoryPostDto.from(memoryPosts);

        Map<Long, List<MemoryPostFamilyMembersDto>> participantsList = new HashMap<>();
        Map<Long, Boolean> isLikedList = new HashMap<>();
        Map<Long, List<MemoryPostFamilyMembersDto>> participantsMap = new HashMap<>();
        Map<Long, Boolean> isLikedMap = new HashMap<>();
        Map<Long, String> thumbnailUrlMap = new HashMap<>();

        for (MemoryPost memoryPost : memoryPosts) {
            // 참여 가족 구성원 맵핑
            List<MemoryPostFamilyMembers> members = memoryPostFamilyMembersRepository.findAllByMemoryPost(memoryPost);
            List<MemoryPostFamilyMembersDto> memberDtoList = MemoryPostFamilyMembersDto.from(members);
            participantsList.put(memoryPost.getId(), memberDtoList);
            List<MemoryPostFamilyMembersDto> memberDtoList = members.stream()
                    .map(usersMapper::toMemoryPostFamilyMembersDto)
                    .toList();
            participantsMap.put(memoryPost.getId(), memberDtoList);

            // 좋아요 여부 맵핑
            boolean isLiked = memoryPostLikeRepository.existsByLikedUserAndMemoryPost(user, memoryPost);
            isLikedMap.put(memoryPost.getId(), isLiked);

            // 썸네일 URL 추출 및 매핑
            String thumbnailUrl = memoryPost.getMemoryPostImages().stream()
                    .filter(img -> img.getImageOrder() != null && img.getImageOrder() == 1)
                    .findFirst()
                    .map(MemoryPostImage::getImageKey)
                    .map(s3Service::generateUrlFromKey)
                    .orElse(null);
            thumbnailUrlMap.put(memoryPost.getId(), thumbnailUrl);
        }

        return  GetRecentMemoryPostResponse.from(memoryPostDtoList, participantsMap, thumbnailUrlMap, isLikedMap);
    }

}
