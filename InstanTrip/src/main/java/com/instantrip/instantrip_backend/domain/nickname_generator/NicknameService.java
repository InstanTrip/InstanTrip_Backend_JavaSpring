package com.instantrip.instantrip_backend.domain.nickname_generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {
    private final NicknameRepository nicknameRepository;

    // prefix, postfix 수는 업데이트 시 임의 변경 가능
    private final int PREFIX_COUNT = 10;
    private final int POSTFIX_COUNT = 10;

    // 랜덤 닉네임 생성 로직
    // 미리 지정된 prefix와 postfix를 랜덤으로 조합하여 닉네임 생성
    // ex) prefix: "멋쟁이", postfix: "여행자" -> "멋쟁이 여행자"
    public String getRandomNickname() {
        Random rnd = new Random();

        int prefixValue = rnd.nextInt(PREFIX_COUNT);
        int postfixValue = rnd.nextInt(POSTFIX_COUNT);

        String prefixIndex = prefixValue < 10 ? "0" + prefixValue : prefixValue + "";
        String postfixIndex = postfixValue < 10 ? "0" + postfixValue : postfixValue + "";

        NicknameModel prefix = nicknameRepository.findByNicknameId("prefix" + prefixIndex);
        NicknameModel postfix = nicknameRepository.findByNicknameId("postfix" + postfixIndex);

        return prefix.getNicknameValue() + " " + postfix.getNicknameValue();
    }
}
