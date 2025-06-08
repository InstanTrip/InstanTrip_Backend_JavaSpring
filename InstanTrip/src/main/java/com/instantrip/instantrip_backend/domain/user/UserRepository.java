package com.instantrip.instantrip_backend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {

    boolean existsByUserId(String userId);

    UserModel findByUserId(String userId);


}
