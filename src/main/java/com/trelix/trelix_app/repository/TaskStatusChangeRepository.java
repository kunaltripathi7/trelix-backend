//package com.trelix.trelix_app.repository;
//
//import com.trelix.trelix_app.entity.TaskStatusChange;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.List;
//
//public interface TaskStatusChangeRepository extends JpaRepository<TaskStatusChange, UUID> {
//
//    List<TaskStatusChange> findByTaskId(UUID taskId);
//
//    List<TaskStatusChange> findByChangedById(UUID userId);
//
//    List<TaskStatusChange> findByChangedAtBetween(LocalDateTime start, LocalDateTime end);
//
//}
