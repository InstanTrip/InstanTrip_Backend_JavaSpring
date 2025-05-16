package com.instantrip.instantrip_backend.global.util.trip_manager;

import com.instantrip.instantrip_backend.domain.trip_plan.TripPlan;

import java.util.List;
import java.util.stream.Collectors;

public class TripMapper {

    public static List<TripPlan.Destination> mapTripResponsesToDestinations(List<TripResponse> tripResponses) {
        return tripResponses.stream()
                .map(response -> TripPlan.Destination.builder()
                        .location(response.getLocation())
                        .date(response.getDate())
                        .nodes(mapNodes(response.getNodes()))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<TripPlan.Destination.Node> mapNodes(List<TripResponse.Node> nodes) {
        return nodes.stream()
                .map(node -> TripPlan.Destination.Node.builder()
                        .destination_type(node.getDestination_type())
                        .destination_id(node.getDestination_id())
                        .memo(node.getMemo())
                        .build())
                .collect(Collectors.toList());
    }
}