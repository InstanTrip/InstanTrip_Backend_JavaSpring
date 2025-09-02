package com.instantrip.instantrip_backend.domain.nickname;

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
//@Table(name = "nickname_examples", schema = "instantrip")
@Table(name = "nickname_prefix", schema = "instantrip")
public class NicknamePrefix {

    @Id
    @Column(name = "prefix_id")
    private String prefixId;

    @Column(name = "prefix_value")
    private String prefixValue;
}
