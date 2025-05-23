package com.instantrip.instantrip_backend.domain.trip_plan.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instantrip.instantrip_backend.domain.invite_code.InviteCodeService;
import com.instantrip.instantrip_backend.domain.trip_plan.TripPlan;
import com.instantrip.instantrip_backend.domain.trip_plan.TripPlanRepository;
import com.instantrip.instantrip_backend.domain.trip_plan.TripPlanService;
import com.instantrip.instantrip_backend.domain.user.UserService;
import com.instantrip.instantrip_backend.global.util.datetime_format.DateTimeTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripWebSocketService {

    // WebSocket 세션을 저장하는 Set
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    // JSON 변환을 위한 ObjectMapper
    private final ObjectMapper objectMapper;

    // 서비스 및 리포지토리
    private final TripPlanService tripPlanService;
    private final TripPlanRepository tripPlanRepository;
    private final InviteCodeService inviteCodeService;
    private final DateTimeTransformer dateTimeTransformer;
    private final UserService userService;

    // WebSocket 세션 추가
    public void registerSession(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket connection established for tripSessionId: {}", session.getId());
    }

    // WebSocket 세션 제거
    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        log.info("WebSocket connection closed for tripSessionId: {}", session.getId());
    }

    public void handleMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String tripId = session.getId();

        log.info("Received message for tripId {}: {}", tripId, payload);

        TripEditRequest tripEditRequest = objectMapper.readValue(payload, TripEditRequest.class);

        switch (tripEditRequest.getMessage_type()) {
            case JOIN:
                handleJoinMessage(session);
                break;

            case PING:
                handlePingMessage(session);
                break;

//            case LEAVE:
//                handleLeaveMessage(session, tripEditRequest);
//                break;

            case UPDATE:
                handleUpdateMessage(session, tripEditRequest);
                break;

            default:
                log.error("Unknown message type: {}", tripEditRequest.getMessage_type());
                break;
        }
    }

    private void handleJoinMessage(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        String tripId = (String) attributes.get("tripId");
        String userId = (String) attributes.get("userId");

        TripPlan plan = tripPlanService.getTripPlanById(tripId);

        String JSON;

        // 여행 정보 검증
        if (verifyAccess(plan, userId)) {

            // 여행 정보 DTO 생성
            TripEditResponse planResponse = createTripEditResponse(plan);

            JSON = objectMapper.writeValueAsString(Map.of(
                    "type", "JOIN",
                    "plan", planResponse
            ));
        }

        else {
            JSON = objectMapper.writeValueAsString(Map.of(
                    "type", "JOIN",
                    "error", "Access denied"
            ));
        }

        // 클라이언트에 여행 정보 전송
        session.sendMessage(new TextMessage(JSON));

    }

    private void handlePingMessage(WebSocketSession session) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.toString().split("\\.")[0];

        String pingJSON = objectMapper.writeValueAsString(Map.of(
                "type", "PONG",
                "time", nowString
        ));

        session.sendMessage(new TextMessage(pingJSON));
    }

    private void handleUpdateMessage(WebSocketSession session, TripEditRequest request) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        String tripId = (String) attributes.get("tripId");
        String userId = (String) attributes.get("userId");

        TripPlan plan = tripPlanService.getTripPlanById(tripId);

        // 여행 정보 검증
        if (!verifyAccess(plan, userId)) {
            return;
        }

        // 여행 정보 업데이트
        String date = request.getDate();
        int index = request.getIndex();
        String destinationId = request.getDestination_id();
        String memo = request.getMemo();


        if (date == null || destinationId == null) {
            log.warn("Missing date or destinationId");
            String errorJSON = objectMapper.writeValueAsString(Map.of(
                    "type", "UPDATE",
                    "error", "Missing date or destinationId"
            ));

            session.sendMessage(new TextMessage(errorJSON));
            return;
        }

        boolean isChanged = false;

        for (TripPlan.Destination destination : plan.getDestinations()) {
            if (destination.getDate().equals(date)) {

                if (index < 0 || index >= destination.getNodes().size()) {
                    log.warn("Invalid index: {}", index);
                    break;
                }

                TripPlan.Destination.Node node = destination.getNodes().get(index);
                node.setDestination_id(destinationId);
                if (memo != null) {
                    node.setMemo(memo);
                }

                isChanged = true;
            }
        }

        if (!isChanged) {
            String errorMessage = "No matching destination found or invalid index";

            log.warn("Cannot Change trip plan: {}", errorMessage);
            String errorJSON = objectMapper.writeValueAsString(Map.of(
                    "type", "UPDATE",
                    "error", errorMessage
            ));

            session.sendMessage(new TextMessage(errorJSON));
            return;
        }

        tripPlanRepository.save(plan);

        // 여행 정보 DTO 생성
        TripEditResponse planResponse = createTripEditResponse(plan);

        String updateJSON = objectMapper.writeValueAsString(Map.of(
                "type", "UPDATE",
                "plan", planResponse
        ));

        // 해당 여행을 수정할 수 있는 세션에 접속한 모든 사용자에게 메시지 전송
        broadcastMessage(plan, updateJSON);
    }

    public int getSessionCount() {
        return sessions.size();
    }

    private boolean verifyAccess(TripPlan plan, String userId) {

        // 여행 정보 조회
        if (plan == null) {
            log.error("Trip plan not found for tripId");
            return false;
        }

        // 접근 권한 확인
        if (!plan.getParticipants().contains(userId)) {
            log.error("Access denied for userId: {} on tripId: {}", userId, plan.getId());
            return false;
        }

        return true;
    }

    private TripEditResponse createTripEditResponse(TripPlan plan) {

        String tripId = plan.getId();

        return TripEditResponse.builder()
                .plan_id(tripId)
                .owner_id(plan.getOwnerId())
                .invite_code(inviteCodeService.getInviteCode(tripId))
                .plan_start(dateTimeTransformer.convDateTime2DateString(plan.getPlanStart()))
                .plan_end(dateTimeTransformer.convDateTime2DateString(plan.getPlanEnd()))
                .dates(plan.getDates())
                .participants(plan.getParticipants().stream()
                        .map(id -> userService.getUserNickname(id))
                        .toList())
                .destinations(plan.getDestinations().stream()
                        .map(destination -> TripEditResponse.Destination.builder()
                                .location(destination.getLocation())
                                .date(destination.getDate())
                                .nodes(destination.getNodes().stream()
                                        .map(node -> TripEditResponse.Destination.Node.builder()
                                                .destination_type(node.getDestination_type())
                                                .destination_id(node.getDestination_id())
                                                .memo(node.getMemo())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    private void broadcastMessage(TripPlan plan, String message) {

        List<String> participantList = plan.getParticipants();
        String tripId = plan.getId();

        for (WebSocketSession session : sessions) {
            try {
                String sessionTripId = (String) session.getAttributes().get("tripId");
                String sessionUserId = (String) session.getAttributes().get("userId");

                if (tripId.equals(sessionTripId) && participantList.contains(sessionUserId)) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException | NullPointerException e) {
                log.warn("Failed to send message to session {}: {}", session.getId(), e.getMessage());
            }
        }
    }
}
