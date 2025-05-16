package com.instantrip.instantrip_backend.domain.trip_plan;


import com.instantrip.instantrip_backend.domain.user.UserService;
import com.instantrip.instantrip_backend.global.util.datetime_format.DateTimeTransformer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TripPlanController {

    @Autowired
    private TripPlanService tripPlanService;

    @Autowired
    private UserService userService;

    @Autowired
    private DateTimeTransformer dateTimeTransformer;



    @Data
    public static class CreatePlanRequest {
        private String start_date;
        private String end_date;
        private List<String> location;
        private String accommodation_taste;
        private String destination_taste;
        private String restaurant_taste;
    }

    @Data
    @Builder
    public static class TripPlanResponse {
        private String tripPlanId;
        private String startDate;
        private String endDate;
        private List<String> locations;
        private String accommodationTaste;
        private String destinationTaste;
        private String restaurantTaste;
    }

    @PostMapping("/create-trip")
    public ResponseEntity<Map<String, Object>> createTrip(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            @RequestBody CreatePlanRequest createPlanRequest) {

        Map<String, Object> response = new HashMap<>();

        // 새 세션을 생성하지 않고 기존 세션 정보를 가져옴
        HttpSession session = request.getSession(false);

        if (session != null) {

            if (principal != null) {
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

                if (initializedTripPlan != null) {
                    response.put("plan_id", initializedTripPlan.getId());
                    response.put("start_date", dateTimeTransformer.convDateTime2DateString(initializedTripPlan.getPlanStart()));
                    response.put("end_date", dateTimeTransformer.convDateTime2DateString(initializedTripPlan.getPlanEnd()));
                    response.put("owner", userService.getUserNickname(initializedTripPlan.getOwnerId()));

                    List<String> participantsIds = initializedTripPlan.getParticipants();
                    response.put("participants", participantsIds.stream()
                            .map(id -> userService.getUserNickname(id))
                            .collect(Collectors.toList()));

                    response.put("invite_code", initializedTripPlan.getInviteCode());
                }

                else {
                    response.put("Error", "Failed to create trip plan");
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
