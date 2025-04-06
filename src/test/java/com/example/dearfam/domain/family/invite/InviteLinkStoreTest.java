package com.example.dearfam.domain.family.invite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InviteLinkStoreTest {

    private InviteLinkStore inviteLinkStore;

    @BeforeEach
    void setUp() {
        inviteLinkStore = new InviteLinkStore();
    }

    @Test
    void 초대코드_생성_저장_조회_성공() {
        // given
        String code = "ABC123";
        Long familyId = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // when
        inviteLinkStore.addCode(code, familyId, expiresAt);
        Long result = inviteLinkStore.getFamilyId(code);

        // then
        assertThat(result).isEqualTo(familyId);
    }

    @Test
    void 만료된_초대코드_조회시_null_반환_및_삭제() {
        // given
        String code = "EXPIRED";
        Long familyId = 2L;
        LocalDateTime expiresAt = LocalDateTime.now().minusMinutes(1); // 이미 만료

        inviteLinkStore.addCode(code, familyId, expiresAt);

        // when
        Long result = inviteLinkStore.getFamilyId(code); // 유효성 체크 포함

        // then
        assertThat(result).isNull(); // 만료되었으니 null
    }

    @Test
    void removeExpiredCode_실행시_만료된_코드_삭제됨() {
        // given
        inviteLinkStore.addCode("VALID", 1L, LocalDateTime.now().plusMinutes(5));
        inviteLinkStore.addCode("EXPIRED", 2L, LocalDateTime.now().minusMinutes(1));

        // when
        inviteLinkStore.removeExpiredCode();

        // then
        assertThat(inviteLinkStore.getFamilyId("VALID")).isEqualTo(1L);
        assertThat(inviteLinkStore.getFamilyId("EXPIRED")).isNull(); // removeExpiredCode로 제거됨
    }
}