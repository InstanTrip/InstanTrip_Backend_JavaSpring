package com.instantrip.instantrip_backend.global.util.trip_manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TripHandler {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${trip.fastapi.url}")
    private String tripApiUrl;

    // FastAPI 서버와 통신하여 여행 계획 생성
    public List<TripResponse> createTrip(TripRequest request) {

        String createTripUrl = tripApiUrl + "create-trip/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TripRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TripResponse[]> response = restTemplate.exchange(
                createTripUrl,
                HttpMethod.POST,
                entity,
                TripResponse[].class
        );

        // 응답 본문이 null일 수 있으므로, null 체크 후 처리
        if (response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }

        else {
            // 응답 본문이 null이면 빈 리스트 반환
            return Collections.emptyList();
        }
    }
}