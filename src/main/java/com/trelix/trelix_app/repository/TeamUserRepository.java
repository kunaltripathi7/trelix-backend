package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamUserRepository extends JpaRepository<TeamUser, TeamUser.TeamUserId> {
    long countByIdTeamId(UUID teamId);

    Optional<TeamUser> findById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    boolean existsById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    void deleteById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    List<TeamUser> findById_TeamId(UUID teamId);

    long countById_TeamIdAndRole(UUID teamId, TeamRole role);
}
