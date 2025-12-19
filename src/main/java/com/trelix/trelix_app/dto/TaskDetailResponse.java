package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskRole;
import com.trelix.trelix_app.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record TaskDetailResponse(
        UUID id,
        UUID teamId,
        String teamName,
        UUID projectId,
        String projectName,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TaskMemberResponse> assignees,
        List<TaskMemberResponse> reviewers
) {
    public static TaskDetailResponse from(Task task, String teamName, String projectName, List<TaskMember> taskMembers) {
        List<TaskMemberResponse> assignees = taskMembers.stream()
                .filter(tm -> tm.getRole() == TaskRole.ASSIGNEE)
                .map(TaskMemberResponse::from)
                .collect(Collectors.toList());

        List<TaskMemberResponse> reviewers = taskMembers.stream()
                .filter(tm -> tm.getRole() == TaskRole.REVIEWER)
                .map(TaskMemberResponse::from)
                .collect(Collectors.toList());

        return new TaskDetailResponse(
                task.getId(),
                task.getTeam().getId(),
                teamName,
                task.getProject().getId(),
                projectName,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                assignees,
                reviewers
        );
    }
}
