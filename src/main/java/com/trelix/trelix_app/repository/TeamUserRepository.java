package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.TeamUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamUserRepository extends JpaRepository<TeamUser, TeamUserKey> {
    long countByIdTeamId(UUID teamId);

    @Query("SELECT tu.id.teamId FROM TeamUser tu WHERE tu.id.userId = :userId")
    List<UUID> findTeamIdsByUserId(@Param("userId") UUID userId);
}
