package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectID);

    List<Task> findByAssignedToId(UUID userId);

    List<Task> findByStatus(String status);

    List<Task> findByProjectIdAndStatus(UUID projectId, String status);

}
