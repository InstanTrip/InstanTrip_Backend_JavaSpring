package com.instantrip.instantrip_backend.domain.nickname;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknamePrefixRepository extends JpaRepository<NicknamePrefix, String> {

    NicknamePrefix findByPrefixId(String prefixId);
}
