package com.trelix.trelix_app.repository;


import com.trelix.trelix_app.entity.TaskMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskMemberRepository extends JpaRepository<TaskMember, UUID> {
    List<TaskMember> findByTaskId(UUID taskId);
    List<TaskMember> findByUserId(UUID userId);
    Optional<TaskMember> findByTaskIdAndUserId(UUID taskId, UUID userId);

}
