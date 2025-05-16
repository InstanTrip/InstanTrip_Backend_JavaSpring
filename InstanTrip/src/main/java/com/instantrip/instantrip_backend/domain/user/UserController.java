package com.instantrip.instantrip_backend.domain.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Data
    public static class NicknameRequest {
        private String nickname;
    }

    @GetMapping("/view-nickname")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
        @AuthenticationPrincipal OAuth2User principal,
        HttpServletRequest request) {

        Map<String, Object> userNickname = new HashMap<>();

        // 새 세션을 생성하지 않고 기존 세션 정보를 가져옴
        HttpSession session = request.getSession(false);

        if (session != null) {

            if (principal != null) {
                // 세션에서 사용자 sub 속성 가져오기
                String userId = principal.getAttribute("sub");
                String nickname = userService.getUserNickname(userId);

                userNickname.put("nickname", nickname);
            }

            else {
                userNickname.put("Error", "Cannot find user info");
                return ResponseEntity.badRequest().body(userNickname);
            }
        }
        else {
            userNickname.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(userNickname);
        }
        return ResponseEntity.ok(userNickname);
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<Map<String, Object>> updateUserNickname(
        @AuthenticationPrincipal OAuth2User principal,
        HttpServletRequest request,
        @RequestBody NicknameRequest nicknameRequest) {

        Map<String, Object> response = new HashMap<>();

        // 새 세션을 생성하지 않고 기존 세션 정보를 가져옴
        HttpSession session = request.getSession(false);

        if (session != null) {

            if (principal != null) {
                // 세션에서 사용자 sub 속성 가져오기
                String userId = principal.getAttribute("sub");

                // 닉네임을 요청 본문에서 가져오기
                String nickname = nicknameRequest.getNickname();

                // 닉네임 업데이트 로직
                boolean isUpdated = userService.updateUserNickname(userId, nickname);
                if (isUpdated) {
                    response.put("Success", "Nickname updated successfully");
                } else {
                    response.put("Error", "Failed to update nickname");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            else {
                response.put("Error", "Cannot find user info");
                return ResponseEntity.badRequest().body(response);
            }
        }
        else {
            response.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}