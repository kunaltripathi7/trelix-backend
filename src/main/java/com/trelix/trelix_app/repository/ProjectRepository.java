//package com.trelix.trelix_app.repository;
//
//import com.trelix.trelix_app.entity.Project;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface ProjectRepository extends JpaRepository<Project, UUID> {
//
//    List<Project> findByTeamId(UUID teamId);
//
//    boolean existsByIdAndTeamId(UUID projectId, UUID teamId);
//
//    @Query("SELECT p FROM Project p JOIN ProjectMember pm ON p.id = pm.project.id WHERE pm.user.id = :userId")
//    List<Project> findAllByUserId(@Param("userId") UUID userId);
//
//    @Query("SELECT p FROM Project p JOIN p.team.teamUsers tu " +
//           "WHERE tu.user.id = :userId AND " +
//           "(:teamId IS NULL OR p.team.id = :teamId) AND " +
//           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
//    Page<Project> searchByUserAccess(@Param("query") String query,
//                                  @Param("teamId") UUID teamId,
//                                  @Param("userId") UUID userId,
//                                  Pageable pageable);
//
//    @Query("SELECT p.id FROM Project p JOIN p.team.teamUsers tu " +
//           "WHERE tu.user.id = :userId")
//    List<UUID> findAccessibleByUserId(@Param("userId") UUID userId);
//}
