package com.example.dearfam.domain.family.service;

import com.example.dearfam.domain.family.dto.InviteLinkDto;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.family.exception.InviteErrorCode;
import com.example.dearfam.domain.family.invite.InviteLinkStore;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InviteService {
    private final UsersRepository usersRepository;
    private final InviteLinkStore inviteLinkStore;

    @Value("${invite.base-url}")
    private String inviteBaseUrl;

    public InviteLinkDto generateInviteLink(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        if (user.getFamily() == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        Long familyId = user.getFamily().getId();

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // 유효기간 1일
        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofDays(1));
        // 테스트용
//        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMinutes(1));
        inviteLinkStore.addCode(code, familyId, expiresAt);

        String link = "https://" + inviteBaseUrl + "/invite" + "?code=" + code;

        return InviteLinkDto.from(link, code, expiresAt);
    }

    public Long validateInviteCodeAndReturnFamilyId(String inviteCode) {
        Long familyId = inviteLinkStore.getFamilyId(inviteCode);
        if (familyId == null) {
            throw InviteErrorCode.INVALID_INVITE_CODE.defaultException();
        }
        return familyId;
    }

}
