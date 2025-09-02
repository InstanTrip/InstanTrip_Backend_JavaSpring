package com.instantrip.instantrip_backend.domain.nickname;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknamePostfixRepository extends JpaRepository<NicknamePostfix, String> {

    NicknamePostfix findByPostfixId(String postfixId);
}
