package com.instantrip.instantrip_backend.domain.trip_plan.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class TripEditResponse {

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

    private String plan_id;
    private String owner_id;
    private String invite_code;
    private String plan_start;
    private String plan_end;
    private int dates;
    private List<String> participants;
    private List<Destination> destinations;
}
