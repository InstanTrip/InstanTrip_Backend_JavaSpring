package com.instantrip.instantrip_backend.global.util.trip_manager;

import lombok.Data;

import java.util.List;

@Data
public class TripResponse {

    @Data
    public static class Node {
        private String destination_type;
        private String destination_id;
        private String memo;
    }

    private String location;
    private String date;
    private List<Node> nodes;
}
