package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.enums.TaskRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskMemberRepository extends JpaRepository<TaskMember, TaskMember.TaskMemberId> {

    List<TaskMember> findByIdTaskId(UUID taskId);

    List<TaskMember> findByIdTaskIdAndRole(UUID taskId, TaskRole role);

    Optional<TaskMember> findByIdTaskIdAndIdUserId(UUID taskId, UUID userId);

    boolean existsByIdTaskIdAndIdUserId(UUID taskId, UUID userId);

    @Query("SELECT tm.role FROM TaskMember tm WHERE tm.id.taskId = :taskId AND tm.id.userId = :userId")
    Optional<TaskRole> findRoleByTaskIdAndUserId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);
}




