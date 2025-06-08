package com.instantrip.instantrip_backend.domain.trip_plan;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TripPlanRepository extends MongoRepository<TripPlan, String> {

    Optional<TripPlan> findById(String id);
    Optional<List<TripPlan>> findByParticipantsContaining(String participantId);
}
