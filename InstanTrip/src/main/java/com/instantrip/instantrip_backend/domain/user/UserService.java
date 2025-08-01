package com.instantrip.instantrip_backend.domain.user;

import com.instantrip.instantrip_backend.domain.nickname.NicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final NicknameService nicknameService;

    // 유저 닉네임 정보 조회
    public String getUserNickname(String userId) {
        UserModel user = userRepository.findByUserId(userId);

        // 유저 정보가 없을 경우 랜덤 닉네임 생성
        if (user == null) {

            String newNickname = nicknameService.getRandomNickname();

            UserModel newUser = UserModel.builder()
                    .userId(userId)
                    .userNickname(newNickname)
                    .build();

            userRepository.save(newUser);
            return newNickname;
        }
        else {
            return user.getUserNickname();
        }
    }

    // 유저 닉네임 정보 변경
    public boolean updateUserNickname(String userId, String newNickname) {
        UserModel user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;
        }

        try {
            user.setUserNickname(newNickname);
            userRepository.save(user);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
