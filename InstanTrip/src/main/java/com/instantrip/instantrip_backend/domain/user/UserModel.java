package com.instantrip.instantrip_backend.domain.user;

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
@Table(name = "user_names", schema = "instantrip")
public class UserModel {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_nickname")
    private String userNickname;
}
