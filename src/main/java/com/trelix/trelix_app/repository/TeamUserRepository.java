package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;


public interface TeamUserRepository extends JpaRepository<TeamUser, UUID> {

    List<TeamUser> findByUserId(UUID userId);

    List<TeamUser> findByTeamID(UUID teamId);

    boolean existsByUserIdAndTeamId(UUID userId,UUID teamId);

    void deleteByUserIdAndTeamId(UUID userId, UUID teamId);

}
