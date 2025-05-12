package com.instantrip.instantrip_backend.domain.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/view-nickname")
    public Map<String, Object> getCurrentUser(
        @AuthenticationPrincipal OAuth2User principal,
        HttpServletRequest request) {

        Map<String, Object> userNickname = new HashMap<>();

        // 새 세션을 생성하지 않고 기존 세션 정보를 가져옴
        HttpSession session = request.getSession(false);

        if (session != null) {

            if (principal != null) {
                // 세션에서 사용자 sub 속성 가져오기
                String userId = (String) principal.getAttribute("sub");
                String nickname = userService.getUserNickname(userId);

                userNickname.put("nickname", nickname);
            }

            else {
                userNickname.put("Error", "Cannot find user info");
            }
        }
        else {
            userNickname.put("Error", "Cannot find session info");
        }
        return userNickname;
    }

    @PostMapping("/update-nickname")
    public Map<String, Object> updateUserNickname(
        @AuthenticationPrincipal OAuth2User principal,
        HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        // 새 세션을 생성하지 않고 기존 세션 정보를 가져옴
        HttpSession session = request.getSession(false);

        if (session != null) {

            if (principal != null) {
                // 세션에서 사용자 sub 속성 가져오기
                String userId = (String) principal.getAttribute("sub");


                // 닉네임 업데이트 로직
            }

            else {
                response.put("Error", "Cannot find user info");
            }
        }
        else {
            response.put("Error", "Cannot find session info");
        }

        return response;
    }
}
