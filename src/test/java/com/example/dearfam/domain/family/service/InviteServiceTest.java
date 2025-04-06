package com.example.dearfam.domain.family.service;

import com.example.dearfam.domain.family.dto.InviteLinkResponse;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.InviteException;
import com.example.dearfam.domain.family.invite.InviteLinkStore;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @InjectMocks
    private InviteService inviteService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private InviteLinkStore inviteLinkStore;

    @Test
    void 초대링크_정상생성() {
        // given
        Long userId = 1L;
        Long familyId = 10L;
        String baseUrl = "http://localhost.com/invite";
        Users mockUser = new Users();
        Family mockFamily = new Family();
        mockFamily.setId(familyId);
        mockUser.setFamily(mockFamily);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        ReflectionTestUtils.setField(inviteService, "inviteBaseUrl", baseUrl);

        // when
        InviteLinkResponse response = inviteService.generateInviteLink(userId);
        System.out.println(response.getLink());

        // then
        assertThat(response.getCode()).hasSize(6);
        assertThat(response.getLink()).startsWith(baseUrl);
        verify(inviteLinkStore).addCode(anyString(), eq(familyId), any(LocalDateTime.class));
    }

    @Test
    void 초대코드_유효성검증_성공() {
        // given
        String code = "ABC123";
        Long familyId = 10L;
        when(inviteLinkStore.getFamilyId(code)).thenReturn(familyId);

        // when
        Long result = inviteService.validateInviteCodeAndReturnFamilyId(code);

        // then
        assertThat(result).isEqualTo(familyId);
    }

    @Test
    void 초대코드_유효성검증_실패() {
        // given
        String code = "EXPIRED";
        when(inviteLinkStore.getFamilyId(code)).thenReturn(null);

        // expect
        assertThatThrownBy(() -> inviteService.validateInviteCodeAndReturnFamilyId(code))
                .isInstanceOf(InviteException.class)
                .hasMessageContaining("유효하지 않은 초대 코드");
    }
}