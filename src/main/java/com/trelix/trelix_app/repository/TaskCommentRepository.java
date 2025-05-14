package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, UUID> {

    List<TaskComment> findByTaskId(UUID taskId);

    List<TaskComment> findByUserId(UUID userID);

}
