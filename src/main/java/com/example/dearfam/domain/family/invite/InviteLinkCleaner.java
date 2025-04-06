package com.example.dearfam.domain.family.invite;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InviteLinkCleaner {
    private final InviteLinkStore inviteLinkStore;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void cleanExpiredCode() {
//        inviteLinkStore.printAllInviteLinks();
        inviteLinkStore.removeExpiredCode();
    }
}
