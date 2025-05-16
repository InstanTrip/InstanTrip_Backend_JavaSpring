package com.instantrip.instantrip_backend.global.util.trip_manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripResponse {

    @Data
    @AllArgsConstructor
    public static class Node {

        private String destination_type;
        private String destination_id;
        private String memo;

    }

    private String location;
    private String date;
    private List<Node> nodes;
}
