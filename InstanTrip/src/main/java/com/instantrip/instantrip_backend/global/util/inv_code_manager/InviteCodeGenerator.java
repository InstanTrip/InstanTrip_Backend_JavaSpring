package com.instantrip.instantrip_backend.global.util.inv_code_manager;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class InviteCodeGenerator {

    public static String generateRandomInviteCode() {

        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
        String base64Uuid = Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);

        return base64Uuid.substring(0, 15); // 15자리 코드 생성
    }
}
