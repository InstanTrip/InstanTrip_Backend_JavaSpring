package com.instantrip.instantrip_backend.domain.trip_plan;

import com.instantrip.instantrip_backend.domain.user.UserService;
import com.instantrip.instantrip_backend.global.util.trip_manager.TripHandler;
import com.instantrip.instantrip_backend.global.util.trip_manager.TripMapper;
import com.instantrip.instantrip_backend.global.util.trip_manager.TripRequest;
import com.instantrip.instantrip_backend.global.util.trip_manager.TripResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlanService {

    private final TripPlanRepository tripPlanRepository;
    private final UserService userService;
    private final TripHandler tripHandler;

    // 초대 코드 생성 (랜덤 문자열)
//    private String generateInviteCode() {
//    }

    // 여행 계획 생성
    public TripPlan createTripPlan(String userId, String startDate, String endDate, List<String> locations, List<String> accTaste, List<String> destTaste, List<String> restTaste) {
        TripRequest tripRequest = new TripRequest(
                startDate,
                endDate,
                locations,
                accTaste,
                destTaste,
                restTaste
        );

        try {
            List<TripResponse> tripResponses = tripHandler.createTrip(tripRequest);

            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atStartOfDay();
            int dates = (int) ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) + 1;
            List<String> participants = List.of(userId);

            // 여행 계획 생성 로직
            List<TripPlan.Destination> destinations = TripMapper.mapTripResponsesToDestinations(tripResponses);

            TripPlan tripPlan = TripPlan.builder()
                    .ownerId(userId)
                    .planStart(start)
                    .planEnd(end)
                    .dates(dates)
                    .participants(participants)
                    .destinations(destinations)
                    .inviteCode("TestInviteCodeExample") // 이건 아직 구현되지 않음
                    .build();

            return tripPlanRepository.save(tripPlan);

        } catch (Exception e) {
            // 예외 처리 (예: 서버 다운, 연결 문제 등)
            System.out.println("여행 계획을 생성하는 동안 오류가 발생했습니다.\n" + e);
            return null;
        }
    }

    // 여행 조회(여행 ID 기반)
    public TripPlan getTripPlanById(String tripPlanId) {
        return tripPlanRepository.findById(tripPlanId).orElse(null);
    }

    // 여행 조회(초대 코드 기반)
    public TripPlan getTripPlanByInviteCode(String inviteCode) {
        return tripPlanRepository.findByInviteCode(inviteCode);
    }

    // 여행 리스트 조회(참가자 ID 기반)
    public List<TripPlan> getTripPlansByParticipantId(String participantId) {
        return tripPlanRepository.findByParticipantsContaining(participantId);
    }

    // 여행 업데이트
    public TripPlan updateTripPlan(String tripPlanId, TripPlan tripPlan) {
        TripPlan existingTripPlan = tripPlanRepository.findById(tripPlanId).orElse(null);

        if (existingTripPlan == null) {
            return null;
        }


        tripPlan.setId(existingTripPlan.getId());

        tripPlanRepository.save(tripPlan);

        return existingTripPlan;
    }

    // 여행 삭제
    public boolean deleteTripPlan(String tripPlanId) {
        TripPlan existingTripPlan = tripPlanRepository.findById(tripPlanId).orElse(null);

        if (existingTripPlan == null) {
            return false;
        }

        tripPlanRepository.delete(existingTripPlan);
        return true;
    }


}
