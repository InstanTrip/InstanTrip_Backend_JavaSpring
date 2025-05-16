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

        public Taste(String accTaste, String destTaste, String restTaste) {
            this.accommodation_taste = List.of(accTaste.split(","));
            this.destination_taste = List.of(destTaste.split(","));
            this.restaurant_taste = List.of(restTaste.split(","));
        }
    }

    private String start_date;
    private String end_date;
    private List<String> locations;
    private Taste taste;

    public TripRequest(String start_date, String end_date, List<String> locations, String accTaste, String destTaste, String restTaste) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.locations = locations;
        this.taste = new Taste(accTaste, destTaste, restTaste);
    }
}
