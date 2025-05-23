package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);

}
