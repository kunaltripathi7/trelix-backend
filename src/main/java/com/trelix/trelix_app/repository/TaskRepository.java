//package com.trelix.trelix_app.repository;
//
//import com.trelix.trelix_app.entity.Task;
//import com.trelix.trelix_app.enums.TaskPriority;
//import com.trelix.trelix_app.enums.TaskStatus;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.UUID;
//
//@Repository
//public interface TaskRepository extends JpaRepository<Task, UUID> {
//
//    Page<Task> findByTeamId(UUID teamId, Pageable pageable);
//
//    Page<Task> findByTeamIdAndProjectId(UUID teamId, UUID projectId, Pageable pageable);
//
//    Page<Task> findByTeamIdAndStatus(UUID teamId, TaskStatus status, Pageable pageable);
//
//    Page<Task> findByTeamIdAndPriority(UUID teamId, TaskPriority priority, Pageable pageable);
//
//    @Query("SELECT t FROM Task t WHERE t.teamId = :teamId " +
//           "AND (:projectId IS NULL OR t.projectId = :projectId) " +
//           "AND (:status IS NULL OR t.status = :status) " +
//           "AND (:priority IS NULL OR t.priority = :priority)")
//    Page<Task> findByFilters(@Param("teamId") UUID teamId,
//                             @Param("projectId") UUID projectId,
//                             @Param("status") TaskStatus status,
//                             @Param("priority") TaskPriority priority,
//                             Pageable pageable);
//
//    @Query("SELECT t FROM Task t JOIN t.team.teamUsers tu " +
//           "WHERE tu.user.id = :userId AND " +
//           "(:projectId IS NULL OR t.project.id = :projectId) AND " +
//           "(:status IS NULL OR t.status = :status) AND " +
//           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
//    Page<Task> searchByUserAccess(@Param("query") String query,
//                                   @Param("projectId") UUID projectId,
//                                   @Param("status") TaskStatus status,
//                                   @Param("userId") UUID userId,
//                                   Pageable pageable);
//}
