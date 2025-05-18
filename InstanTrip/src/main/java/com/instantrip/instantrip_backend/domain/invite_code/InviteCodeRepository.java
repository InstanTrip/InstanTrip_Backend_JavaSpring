package com.instantrip.instantrip_backend.domain.invite_code;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, String> {

    boolean existsByInviteCode(String inviteCode);
    Optional<InviteCode> findByInviteCode(String inviteCode);
    InviteCode findByTripId(String tripId);
}
