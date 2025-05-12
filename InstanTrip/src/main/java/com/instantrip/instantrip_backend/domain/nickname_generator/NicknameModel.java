package com.instantrip.instantrip_backend.domain.nickname_generator;


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
@Table(name = "nickname_examples", schema = "instantrip")
public class NicknameModel {

    @Id
    @Column(name = "ex_id")
    private String nicknameId;

    @Column(name = "ex_value")
    private String nicknameValue;
}
