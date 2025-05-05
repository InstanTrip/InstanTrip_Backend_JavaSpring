package com.instantrip.instantrip_backend.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CognitoUserController {

  @GetMapping("/user")
  public Map<String, Object> getCurrentUser(
      @AuthenticationPrincipal OAuth2User principal,
      HttpServletRequest request) {

    Map<String, Object> userInfo = new HashMap<>();

    // 세션 정보 가져오기
    HttpSession session = request.getSession(false); // 새 세션 생성하지 않고 기존 세션만 가져옴

    if (session != null) {
      userInfo.put("sessionId", session.getId());
      userInfo.put("sessionCreationTime", session.getCreationTime());
      userInfo.put("sessionLastAccessedTime", session.getLastAccessedTime());
      userInfo.put("sessionMaxInactiveInterval", session.getMaxInactiveInterval());
    }

    if (principal != null) {
      //userInfo.put("name", principal.getName());
//      userInfo.put("name", principal.get
      userInfo.put("email", principal.getAttribute("email"));
      userInfo.put("sub", principal.getAttribute("sub"));

      // 모든 사용자 속성 추가 (옵션)
      userInfo.put("attributes", principal.getAttributes());

      // 권한 정보 추가 (옵션)
      userInfo.put("authorities", principal.getAuthorities().toString());

      userInfo.put("test", principal);

      userInfo.put("aa", principal.getName());
      userInfo.put("ab", principal.getClass().getName());
      userInfo.put("ab", principal.getClass().getFields());
//      userInfo.put("ab", principal.getClass().getName());
    }

    return userInfo;
  }
}
