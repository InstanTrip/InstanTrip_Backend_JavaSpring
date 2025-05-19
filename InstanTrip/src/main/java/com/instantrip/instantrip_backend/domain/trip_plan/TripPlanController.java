package com.instantrip.instantrip_backend.domain.trip_plan;


import com.instantrip.instantrip_backend.domain.invite_code.InviteCodeService;
import com.instantrip.instantrip_backend.domain.user.UserService;
import com.instantrip.instantrip_backend.global.util.datetime_format.DateTimeTransformer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TripPlanController {

    @Autowired
    private TripPlanService tripPlanService;

    @Autowired
    private UserService userService;

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private DateTimeTransformer dateTimeTransformer;

    // 여행 계획 생성 요청 DTO
    @Data
    public static class CreatePlanRequest {
        private String start_date;
        private String end_date;
        private List<String> location;
        private List<String> accommodation_taste;
        private List<String> destination_taste;
        private List<String> restaurant_taste;
    }

    // 리스트 표시용 여행 계획 응답 DTO
    @Data
    @Builder
    public static class TripPlanSimpleResponse {

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Taste {
            private List<String> accommodation_taste;
            private List<String> destination_taste;
            private List<String> restaurant_taste;

        }

        private String plan_id;
        private String plan_start;
        private String plan_end;
        private int dates;
        private List<String> location;

        private Taste taste;
    }

    // 여행 계획 응답 DTO
    @Data
    @Builder
    public static class TripPlanResponse {

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Destination {

            @Data
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Node {

                private String destination_type;
                private String destination_id;
                private String memo;

            }

            private String location;
            private String date;
            private List<Node> nodes;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Taste {
            private List<String> accommodation_taste;
            private List<String> destination_taste;
            private List<String> restaurant_taste;

        }

        private String plan_id;
        private String owner_id;
        private String invite_code;
        private String plan_start;
        private String plan_end;
        private int dates;
        private List<String> participants;
        private List<Destination> destinations;
        private Taste taste;
    }

    // 여행 초대 수락 요청 DTO
    @Data
    public static class AcceptRequest {
        private String code;
    }

    // 여행 계획 삭제 요청 DTO
    @Data
    public static class DeleteRequest {
        private String id;
    }

    // 여행 계획 생성 API
    @PostMapping("/create-trip")
    public ResponseEntity<Map<String, Object>> createTrip(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            @RequestBody CreatePlanRequest createPlanRequest) {

        Map<String, Object> response = new HashMap<>();

        // 세션 검증 로직
        HttpSession session = request.getSession(false);

        if (session == null)  {
            response.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(response);
        }

        if (principal == null) {
            response.put("Error", "Cannot find user info");
            return ResponseEntity.badRequest().body(response);
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");
        TripPlan initializedTripPlan = tripPlanService.createTripPlan(
                userId,
                createPlanRequest.getStart_date(),
                createPlanRequest.getEnd_date(),
                createPlanRequest.getLocation(),
                createPlanRequest.getAccommodation_taste(),
                createPlanRequest.getDestination_taste(),
                createPlanRequest.getRestaurant_taste()
        );

        if (initializedTripPlan == null) {
            response.put("Error", "Failed to create trip plan");
            return ResponseEntity.badRequest().body(response);
        }

        String tripId = initializedTripPlan.getId();
        inviteCodeService.getRandomInviteCode(tripId);

        response.put("Success", "Trip plan created successfully");
        response.put("plan_id", tripId);

        return ResponseEntity.ok(response);
    }

    // 여행 계획 목록 조회 API
    @GetMapping("/trip-list")
    public ResponseEntity<?> getTripPlanList(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request) {

        List<TripPlanSimpleResponse> response;
        Map<String, Object> errLog = new HashMap<>();

        // 세션 검증 로직
        HttpSession session = request.getSession(false);

        if (session == null)  {
            errLog.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(errLog);
        }

        if (principal == null) {
            errLog.put("Error", "Cannot find user info");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");
        List<TripPlan> plans = tripPlanService.getTripPlansByParticipantId(userId);

        if (plans == null) {
            errLog.put("Error", "Failed to create trip plan");
            return ResponseEntity.badRequest().body(errLog);
        }

        if (plans.isEmpty()) {
            errLog.put("Error", "No trip plans found");
            return ResponseEntity.ok(errLog);
        }

        response = plans.stream()
                .map(plan -> TripPlanSimpleResponse.builder()
                        .plan_id(plan.getId())
                        .plan_start(dateTimeTransformer.convDateTime2DateString(plan.getPlanStart()))
                        .plan_end(dateTimeTransformer.convDateTime2DateString(plan.getPlanEnd()))
                        .dates(plan.getDates())
                        .location(plan.getDestinations().stream()
                                .map(TripPlan.Destination::getLocation)
                                .distinct()
                                .toList())
                        .taste(TripPlanSimpleResponse.Taste.builder()
                                .accommodation_taste(plan.getTastes().getAccommodation_taste())
                                .destination_taste(plan.getTastes().getDestination_taste())
                                .restaurant_taste(plan.getTastes().getRestaurant_taste())
                                .build())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    // 특정 여행 계획 상세 조회 API
    @GetMapping("/trip")
    public ResponseEntity<?> getTripPlanDetail(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            @RequestParam("id") String planId) {

        TripPlanResponse response;
        Map<String, Object> errLog = new HashMap<>();

        // 세션 검증 로직
        HttpSession session = request.getSession(false);

        if (session == null)  {
            errLog.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(errLog);
        }

        if (principal == null) {
            errLog.put("Error", "Cannot find user info");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 여행 정보 조회
        TripPlan plan = tripPlanService.getTripPlanById(planId);

        if (plan == null) {
            errLog.put("Error", "Failed to get trip plan");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");

        // 접근 권한 확인 (해당 여행에 참여 중인지)
        if (!plan.getParticipants().contains(userId)) {
            errLog.put("Error", "User does not have access to this trip plan");
            return ResponseEntity.status(403).body(errLog);
        }

        // 여행 계획 응답 DTO 생성
        response = TripPlanResponse.builder()
                .plan_id(planId)
                .owner_id(plan.getOwnerId())
                .invite_code(inviteCodeService.getInviteCode(planId))
                .plan_start(dateTimeTransformer.convDateTime2DateString(plan.getPlanStart()))
                .plan_end(dateTimeTransformer.convDateTime2DateString(plan.getPlanEnd()))
                .dates(plan.getDates())
                .participants(plan.getParticipants().stream()
                        .map(id -> userService.getUserNickname(id))
                        .toList())
                .destinations(plan.getDestinations().stream()
                        .map(destination -> TripPlanResponse.Destination.builder()
                                .location(destination.getLocation())
                                .date(destination.getDate())
                                .nodes(destination.getNodes().stream()
                                        .map(node -> TripPlanResponse.Destination.Node.builder()
                                                .destination_type(node.getDestination_type())
                                                .destination_id(node.getDestination_id())
                                                .memo(node.getMemo())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .taste(TripPlanResponse.Taste.builder()
                        .accommodation_taste(plan.getTastes().getAccommodation_taste())
                        .destination_taste(plan.getTastes().getDestination_taste())
                        .restaurant_taste(plan.getTastes().getRestaurant_taste())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    // 여행 초대 수락 API
    @PostMapping("/accept-trip")
    public ResponseEntity<?> acceptTrip(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            @RequestBody AcceptRequest acceptRequest) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errLog = new HashMap<>();

        // 세션 검증 로직
        HttpSession session = request.getSession(false);

        if (session == null) {
            errLog.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(errLog);
        }

        if (principal == null) {
            errLog.put("Error", "Cannot find user info");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");

        // 초대 코드로 여행 계획 ID 조회
        String tripId = inviteCodeService.getTripIdByInviteCode(acceptRequest.getCode());

        if (tripId == null) {
            errLog.put("Error", "Failed to get trip plan");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 여행 계획에 참여
        if (tripPlanService.addParticipantToTripPlan(tripId, userId)) {
            response.put("Success", "Trip plan accepted successfully");
            response.put("plan_id", tripId);
        }

        else {
            errLog.put("Error", "Failed to accept trip plan");
            return ResponseEntity.badRequest().body(errLog);
        }

        return ResponseEntity.ok(response);
    }

    // 특정 여행 계획 삭제 API
    @PostMapping("/delete-trip")
    public ResponseEntity<?> deleteTrip(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            @RequestBody DeleteRequest deleteRequest) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> errLog = new HashMap<>();

        // 세션 검증 로직
        HttpSession session = request.getSession(false);

        if (session == null)  {
            errLog.put("Error", "Cannot find session info");
            return ResponseEntity.badRequest().body(errLog);
        }

        if (principal == null) {
            errLog.put("Error", "Cannot find user info");
            return ResponseEntity.badRequest().body(errLog);
        }

        String planId = deleteRequest.getId();

        // 여행 정보 조회
        TripPlan plan = tripPlanService.getTripPlanById(planId);

        if (plan == null) {
            errLog.put("Error", "Failed to get trip plan");
            return ResponseEntity.badRequest().body(errLog);
        }

        // 세션에서 사용자 sub 속성 가져오기
        String userId = principal.getAttribute("sub");

        // 접근 권한 확인 (해당 여행의 소유자인지)
        if (plan.getOwnerId().equals(userId)) {

            if (tripPlanService.deleteTripPlanByOwner(planId)) {
                response.put("Success", "Trip plan deleted successfully");
            }

            else {
                errLog.put("Error", "Failed to delete trip plan");
                return ResponseEntity.badRequest().body(errLog);
            }
        }

        // 접근 권한 확인 (해당 여행의 참여자인지)
        else if (plan.getParticipants().contains(userId)) {

            if (tripPlanService.deleteTripPlanByParticipant(planId, userId)) {
                response.put("Success", "Trip plan deleted successfully");
            }

            else {
                errLog.put("Error", "Failed to delete trip plan");
                return ResponseEntity.badRequest().body(errLog);
            }
        }

        // 접근 권한이 없을 때
        else {
            errLog.put("Error", "User does not have access to this trip plan");
            return ResponseEntity.status(403).body(errLog);
        }

        return ResponseEntity.ok(response);
    }
}
