package com.instantrip.instantrip_backend.domain.trip_plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "trip_plans")
public class TripPlan {

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

    @Id
    private String id;

    @Field("owner_id")
    private String ownerId;

    @Field("plan_start")
    private LocalDateTime planStart;

    @Field("plan_end")
    private LocalDateTime planEnd;

    @Field("dates")
    private int dates;

    @Field("participants")
    private List<String> participants;

    @Field("destinations")
    private List<Destination> destinations;

//    Last Updated는 나중에 구현 예정
//    @Field("last_updated")
//    private String lastUpdated;
}
