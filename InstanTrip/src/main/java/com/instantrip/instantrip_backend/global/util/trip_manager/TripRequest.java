package com.instantrip.instantrip_backend.global.util.trip_manager;

import lombok.Data;

import java.util.List;

@Data
public class TripRequest {

    @Data
    public static class Taste {
        private List<String> accommodation_taste;
        private List<String> destination_taste;
        private List<String> restaurant_taste;
    }

    private String start_date;
    private String end_date;
    private List<String> location;
    private Taste taste;
}
