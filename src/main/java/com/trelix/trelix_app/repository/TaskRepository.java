package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

       @Query("SELECT DISTINCT t FROM Task t " +
                     "LEFT JOIN t.team.teamUsers tu " +
                     "LEFT JOIN t.project.members pu " +
                     "LEFT JOIN t.members tm " +
                     "WHERE (" +
                     "    (tu.user.id = :userId AND (tu.role = TeamRole.ADMIN OR tu.role = TeamRole.OWNER)) " +
                     "    OR " +
                     "    (pu.user.id = :userId AND (pu.role = ProjectRole.ADMIN)) " +
                     "    OR " +
                     "    (tm.user.id = :userId) " +
                     ") " +
                     "AND (:teamId IS NULL OR t.teamId = :teamId) " +
                     "AND (:projectId IS NULL OR t.projectId = :projectId) " +
                     "AND (:status IS NULL OR t.status = :status) " +
                     "AND (:priority IS NULL OR t.priority = :priority) " +
                     "AND (" +
                     "   :query IS NULL " +
                     "   OR :query = '' " +
                     "   OR (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))"
                     +
                     ")")
       Page<Task> findTasksForUser(@Param("userId") UUID userId,
                     @Param("teamId") UUID teamId,
                     @Param("projectId") UUID projectId,
                     @Param("status") TaskStatus status,
                     @Param("priority") TaskPriority priority,
                     @Param("query") String query,
                     Pageable pageable);

       @Query("SELECT t FROM Task t " +
                     "LEFT JOIN FETCH t.team " +
                     "LEFT JOIN FETCH t.project " +
                     "LEFT JOIN FETCH t.members tm " +
                     "LEFT JOIN FETCH tm.user " +
                     "WHERE t.id = :taskId")
       Optional<Task> findTaskDetailById(@Param("taskId") UUID taskId);

       @Query("SELECT t from Task t " +
                     "LEFT JOIN FETCH t.members tm " +
                     "LEFT JOIN FETCH tm.user " +
                     "where t.id = :taskId")
       Optional<Task> findTaskMembersById(@Param("taskId") UUID taskId);

}
