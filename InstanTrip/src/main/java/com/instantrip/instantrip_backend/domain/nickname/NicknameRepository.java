package com.instantrip.instantrip_backend.domain.nickname;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknameRepository extends JpaRepository<Nickname, String> {

    Nickname findByNicknameId(String nicknameId);
}
