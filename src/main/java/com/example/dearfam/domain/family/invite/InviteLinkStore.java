package com.example.dearfam.domain.family.invite;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InviteLinkStore {
    private final Map<String, InviteLinkInfo> inviteLinkMap = new ConcurrentHashMap<>();

    public void addCode(String code, Long familyId, LocalDateTime expiresAt) {
        InviteLinkInfo linkInfo = new InviteLinkInfo(familyId, expiresAt);
        inviteLinkMap.put(code,linkInfo);
    }

    // 이 함수로 함수의 유효성을 검사함. 코드의 정보가 null이거나 만료되었을 때, null값을 리턴함.
    // 아직 유효하다면 familyId를 리턴함.
    public Long getFamilyId(String code) {
        InviteLinkInfo info = inviteLinkMap.get(code);
        if (info == null || info.isExpired()) {
            inviteLinkMap.remove(code);
            return null;
        }
        return info.getFamilyId();
    }

    public void removeExpiredCode() {
        inviteLinkMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

//    public void printAllInviteLinks() {
//        inviteLinkMap.forEach((code, info) -> {
//            System.out.println("Code: " + code + ", FamilyId: " + info.getFamilyId() + ", ExpiresAt: " + info.getExpireAt());
//        });
//    }

}
