package com.instantrip.instantrip_backend.domain.invite_code;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trip_invite_code", schema = "instantrip")
public class InviteCode {

    @Id
    @Column(name="invite_code")
    private String inviteCode;

    @Column(name="trip_id")
    private String tripId;
}
