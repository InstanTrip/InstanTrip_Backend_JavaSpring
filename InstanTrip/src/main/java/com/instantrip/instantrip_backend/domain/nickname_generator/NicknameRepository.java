package com.instantrip.instantrip_backend.domain.nickname_generator;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknameRepository extends JpaRepository<NicknameModel, String> {

    NicknameModel findByNicknameId(String nicknameId);
}
