package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.enums.TaskRole;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, UUID creatorId);

    PagedTaskResponse getTasks(UUID teamId, UUID projectId, TaskStatus status, TaskPriority priority, int page, int size, UUID requesterId);

    TaskDetailResponse getTaskById(UUID taskId, UUID requesterId);

    TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID requesterId);

    TaskResponse updateTaskStatus(UUID taskId, TaskStatus newStatus, UUID requesterId);

    void deleteTask(UUID taskId, UUID requesterId);

    List<TaskMemberResponse> getTaskMembers(UUID taskId, UUID requesterId);

    TaskMemberResponse assignMember(UUID taskId, AssignTaskMemberRequest request, UUID requesterId);

    TaskMemberResponse updateMemberRole(UUID taskId, UUID userId, TaskRole newRole, UUID requesterId);

    void removeMember(UUID taskId, UUID userId, UUID requesterId);
}
