package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamUserRepository extends JpaRepository<TeamUser, TeamUser.TeamUserId> {

    Optional<TeamUser> findById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    boolean existsById_TeamIdAndId_UserIdAndRole(UUID teamId, UUID userId, TeamRole teamRole);

    boolean existsById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    void deleteById_TeamIdAndId_UserId(UUID teamId, UUID userId);

    List<TeamUser> findById_TeamId(UUID teamId);

    long countById_TeamId(UUID teamId);
}




