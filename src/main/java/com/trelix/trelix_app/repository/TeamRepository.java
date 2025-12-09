package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    //Jpql operates on java entities not db tables
    @Query("SELECT t FROM Team t JOIN t.teamUsers tu WHERE tu.user.id = :userId")
    List<Team> findTeamsByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Team t JOIN t.teamUsers tu " +
           "WHERE tu.user.id = :userId AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Team> searchByUserAccess(@Param("query") String query,
                                   @Param("userId") UUID userId,
                                   Pageable pageable);

    @Query("SELECT t FROM Team t " +
           "LEFT JOIN FETCH t.teamUsers tu LEFT JOIN FETCH tu.user " +
           "LEFT JOIN FETCH t.projects " +
           "LEFT JOIN FETCH t.channels WHERE t.id = :teamId")
    Optional<Team> findDetailsById(@Param("teamId") UUID teamId);
}
