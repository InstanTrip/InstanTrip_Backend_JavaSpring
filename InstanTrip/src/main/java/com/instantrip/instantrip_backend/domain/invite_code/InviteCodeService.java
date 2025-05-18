package com.instantrip.instantrip_backend.domain.invite_code;

import com.instantrip.instantrip_backend.global.util.inv_code_manager.InviteCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;

    // 초대 코드 중복 검사 수
    private static final int MAX_RETRY = 10;

    public String getRandomInviteCode(String tripId) {

        for (int i = 0; i < MAX_RETRY; i++) {
            String invCode = InviteCodeGenerator.generateRandomInviteCode();

            if (!inviteCodeRepository.existsByInviteCode(invCode)) {

                InviteCode inviteCode = InviteCode.builder()
                        .inviteCode(invCode)
                        .tripId(tripId)
                        .build();

                inviteCodeRepository.save(inviteCode);

                return invCode;
            }
        }
        return null;
    }

    public String getInviteCode(String tripId) {

        InviteCode code = inviteCodeRepository.findByTripId(tripId);

        if (code != null) {
            return code.getInviteCode();
        }

        return null;

    }

    public String getTripIdByInviteCode(String inviteCode) {

        InviteCode code = inviteCodeRepository.findByInviteCode(inviteCode).orElse(null);

        if (code != null) {
            return code.getTripId();
        }

        return null;
    }
}

