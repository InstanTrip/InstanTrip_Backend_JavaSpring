package com.instantrip.instantrip_backend.domain.trip_plan.websocket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripEditRequest {

    private MessageType message_type;
    private String date;
    private Integer index;
    private String destination_type;
    private String destination_id;
    private String memo;
}
