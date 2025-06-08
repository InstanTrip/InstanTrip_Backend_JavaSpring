package com.instantrip.instantrip_backend.domain.trip_plan.websocket;

import com.instantrip.instantrip_backend.domain.trip_plan.TripPlan;
import com.instantrip.instantrip_backend.domain.trip_plan.TripPlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TripHandshakeInterceptor implements HandshakeInterceptor {

    private final TripPlanService tripPlanService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }


        // 세션 검증 로직
        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            return false;
        }
        OAuth2User principal = extractPrincipalFromSession(session) != null
                ? extractPrincipalFromSession(session)
                : null;

        if (principal == null) {
            log.error("[❌] No authenticated user found in session");
            return false;
        }

        // 쿼리 파라미터에서 tripId 추출
        String query = request.getURI().getQuery(); // e.g., tripId=123
        if (query == null || !query.contains("tripId=")) {
            log.error("[❌] Missing tripId in query");
            return false;
        }

        String tripId = Arrays.stream(query.split("&"))
                .map(s -> s.split("="))
                .filter(kv -> kv.length == 2 && kv[0].equals("tripId"))
                .map(kv -> kv[1])
                .findFirst()
                .orElse(null);

        if (tripId == null) {
            log.error("[❌] Invalid or missing tripId");
            return false;
        }

        // 여행 정보 조회
        TripPlan plan = tripPlanService.getTripPlanById(tripId);

        if (plan == null) {
            log.error("[❌] Trip not found");
            return false;
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");

        // 접근 권한 확인 (해당 여행에 참여 중인지)
        if (!plan.getParticipants().contains(userId)) {
            log.error("[❌] User not authorized to access this trip");
            return false;
        }

        // 세션에 사용자 ID 및 여행 ID 저장
        attributes.put("userId", userId);
        attributes.put("tripId", tripId);

        log.info("[✅] Authenticated user {} for trip {}", userId, tripId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 필요 시 로깅 등 처리
    }

    // 세션에서 SPRING_SECURITY_CONTEXT → Authentication → OAuth2User 추출
    private OAuth2User extractPrincipalFromSession(HttpSession session) {
        Object context = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context instanceof SecurityContext securityContext) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
                return oAuth2User;
            }
        }
        return null;
    }
}
