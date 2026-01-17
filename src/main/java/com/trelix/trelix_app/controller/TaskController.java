package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.AssignTaskMemberRequest;
import com.trelix.trelix_app.dto.request.CreateTaskRequest;
import com.trelix.trelix_app.dto.response.PagedTaskResponse;
import com.trelix.trelix_app.dto.response.TaskDetailResponse;
import com.trelix.trelix_app.dto.response.TaskMemberResponse;
import com.trelix.trelix_app.dto.response.TaskResponse;
import com.trelix.trelix_app.dto.request.UpdateTaskMemberRoleRequest;
import com.trelix.trelix_app.dto.request.UpdateTaskRequest;
import com.trelix.trelix_app.dto.request.UpdateTaskStatusRequest;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tasks")
@Validated
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management with filtering, status updates, and assignments")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task in a team or project")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskResponse taskResponse = taskService.createTask(request, currentUser.getId());
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get tasks", description = "Get paginated tasks with optional filters for team, project, status, priority")
    public ResponseEntity<PagedTaskResponse> getTasks(
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String query,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PagedTaskResponse tasks = taskService.getTasks(teamId, projectId, status, priority, page, size,
                currentUser.getId(), query);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task details", description = "Get detailed information about a specific task")
    public ResponseEntity<TaskDetailResponse> getTaskById(
            @PathVariable @NotNull UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskDetailResponse taskDetail = taskService.getTaskById(taskId, currentUser.getId());
        return ResponseEntity.ok(taskDetail);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Update task details like title, description, due date")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable @NotNull UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskResponse updatedTask = taskService.updateTask(taskId, request, currentUser.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Update task status", description = "Change task status (TODO, IN_PROGRESS, DONE, etc.)")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable @NotNull UUID taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskResponse updatedTask = taskService.updateTaskStatus(taskId, request.status(), currentUser.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @NotNull UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        taskService.deleteTask(taskId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/members")
    @Operation(summary = "Get task members", description = "Get list of users assigned to a task")
    public ResponseEntity<List<TaskMemberResponse>> getTaskMembers(
            @PathVariable @NotNull UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<TaskMemberResponse> members = taskService.getTaskMembers(taskId, currentUser.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{taskId}/members")
    @Operation(summary = "Assign member", description = "Assign a user to a task. Sends notification to assignee.")
    public ResponseEntity<TaskMemberResponse> assignMember(
            @PathVariable @NotNull UUID taskId,
            @Valid @RequestBody AssignTaskMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskMemberResponse newMember = taskService.assignMember(taskId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}/members/{userId}")
    @Operation(summary = "Update member role", description = "Change a task member's role")
    public ResponseEntity<TaskMemberResponse> updateMemberRole(
            @PathVariable @NotNull UUID taskId,
            @PathVariable @NotNull UUID userId,
            @Valid @RequestBody UpdateTaskMemberRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        TaskMemberResponse updatedMember = taskService.updateMemberRole(taskId, userId, request.role(),
                currentUser.getId());
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{taskId}/members/{userId}")
    @Operation(summary = "Remove member", description = "Unassign a user from a task")
    public ResponseEntity<Void> removeMember(
            @PathVariable @NotNull UUID taskId,
            @PathVariable @NotNull UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        taskService.removeMember(taskId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
