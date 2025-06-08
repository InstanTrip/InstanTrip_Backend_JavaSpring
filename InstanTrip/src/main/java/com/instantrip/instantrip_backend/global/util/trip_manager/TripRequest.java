package com.instantrip.instantrip_backend.global.util.trip_manager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class TripRequest {

    @Data
    @AllArgsConstructor
    public static class Taste {
        private List<String> accommodation_taste;
        private List<String> destination_taste;
        private List<String> restaurant_taste;

    }

    private String start_date;
    private String end_date;
    private List<String> location;
    private Taste taste;

    public TripRequest(String start_date, String end_date, List<String> location, List<String> accTaste, List<String> destTaste, List<String> restTaste) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.location = location;
        this.taste = new Taste(accTaste, destTaste, restTaste);
    }
}
