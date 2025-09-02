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
@Table(name = "nickname_postfix", schema = "instantrip")
public class NicknamePostfix {

    @Id
    @Column(name = "postfix_id")
    private String postfixId;

    @Column(name = "postfix_value")
    private String postfixValue;
}
