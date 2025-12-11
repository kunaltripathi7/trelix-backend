//package com.trelix.trelix_app.dto;
//
//import com.trelix.trelix_app.entity.Task;
//import com.trelix.trelix_app.enums.TaskPriority;
//import com.trelix.trelix_app.enums.TaskStatus;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//public record TaskSearchResponse(
//        UUID id,
//        UUID teamId,
//        UUID projectId,
//        String title,
//        String description,
//        TaskStatus status,
//        TaskPriority priority,
//        LocalDate dueDate,
//        LocalDateTime updatedAt
//) {
//    public static TaskSearchResponse from(Task task) {
//        return new TaskSearchResponse(
//                task.getId(),
//                task.getTeam().getId(),
//                task.getProject() != null ? task.getProject().getId() : null,
//                task.getTitle(),
//                task.getDescription(),
//                task.getStatus(),
//                task.getPriority(),
//                task.getDueDate(),
//                task.getUpdatedAt()
//        );
//    }
//}
