//package com.trelix.trelix_app.controller;
//
//import com.trelix.trelix_app.dto.AssignTaskMemberRequest;
//import com.trelix.trelix_app.dto.CreateTaskRequest;
//import com.trelix.trelix_app.dto.PagedTaskResponse;
//import com.trelix.trelix_app.dto.TaskDetailResponse;
//import com.trelix.trelix_app.dto.TaskMemberResponse;
//import com.trelix.trelix_app.dto.TaskResponse;
//import com.trelix.trelix_app.dto.UpdateTaskMemberRoleRequest;
//import com.trelix.trelix_app.dto.UpdateTaskRequest;
//import com.trelix.trelix_app.dto.UpdateTaskStatusRequest;
//import com.trelix.trelix_app.enums.TaskPriority;
//import com.trelix.trelix_app.enums.TaskStatus;
//import com.trelix.trelix_app.service.TaskService;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
////
////@RestController
////@RequestMapping("/api/v1/tasks")
////@Validated
//public class TaskController {
//
//    private final TaskService taskService;
//
//    public TaskController(TaskService taskService) {
//        this.taskService = taskService;
//    }
//
//    @PostMapping
//    public ResponseEntity<TaskResponse> createTask(
//            @Valid @RequestBody CreateTaskRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID creatorId = UUID.fromString(jwt.getSubject());
//        TaskResponse taskResponse = taskService.createTask(request, creatorId);
//        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<PagedTaskResponse> getTasks(
//            @RequestParam @NotNull UUID teamId,
//            @RequestParam(required = false) UUID projectId,
//            @RequestParam(required = false) TaskStatus status,
//            @RequestParam(required = false) TaskPriority priority,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        PagedTaskResponse tasks = taskService.getTasks(teamId, projectId, status, priority, page, size, requesterId);
//        return ResponseEntity.ok(tasks);
//    }
//
//    @GetMapping("/{taskId}")
//    public ResponseEntity<TaskDetailResponse> getTaskById(
//            @PathVariable @NotNull UUID taskId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        TaskDetailResponse taskDetail = taskService.getTaskById(taskId, requesterId);
//        return ResponseEntity.ok(taskDetail);
//    }
//
//    @PutMapping("/{taskId}")
//    public ResponseEntity<TaskResponse> updateTask(
//            @PathVariable @NotNull UUID taskId,
//            @Valid @RequestBody UpdateTaskRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        TaskResponse updatedTask = taskService.updateTask(taskId, request, requesterId);
//        return ResponseEntity.ok(updatedTask);
//    }
//
//    @PatchMapping("/{taskId}/status")
//    public ResponseEntity<TaskResponse> updateTaskStatus(
//            @PathVariable @NotNull UUID taskId,
//            @Valid @RequestBody UpdateTaskStatusRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        TaskResponse updatedTask = taskService.updateTaskStatus(taskId, request.status(), requesterId);
//        return ResponseEntity.ok(updatedTask);
//    }
//
//    @DeleteMapping("/{taskId}")
//    public ResponseEntity<Void> deleteTask(
//            @PathVariable @NotNull UUID taskId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        taskService.deleteTask(taskId, requesterId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{taskId}/members")
//    public ResponseEntity<List<TaskMemberResponse>> getTaskMembers(
//            @PathVariable @NotNull UUID taskId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        List<TaskMemberResponse> members = taskService.getTaskMembers(taskId, requesterId);
//        return ResponseEntity.ok(members);
//    }
//
//    @PostMapping("/{taskId}/members")
//    public ResponseEntity<TaskMemberResponse> assignMember(
//            @PathVariable @NotNull UUID taskId,
//            @Valid @RequestBody AssignTaskMemberRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        TaskMemberResponse newMember = taskService.assignMember(taskId, request, requesterId);
//        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{taskId}/members/{userId}")
//    public ResponseEntity<TaskMemberResponse> updateMemberRole(
//            @PathVariable @NotNull UUID taskId,
//            @PathVariable @NotNull UUID userId,
//            @Valid @RequestBody UpdateTaskMemberRoleRequest request,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        TaskMemberResponse updatedMember = taskService.updateMemberRole(taskId, userId, request.role(), requesterId);
//        return ResponseEntity.ok(updatedMember);
//    }
//
//    @DeleteMapping("/{taskId}/members/{userId}")
//    public ResponseEntity<Void> removeMember(
//            @PathVariable @NotNull UUID taskId,
//            @PathVariable @NotNull UUID userId,
//            @AuthenticationPrincipal Jwt jwt) {
//        UUID requesterId = UUID.fromString(jwt.getSubject());
//        taskService.removeMember(taskId, userId, requesterId);
//        return ResponseEntity.noContent().build();
//    }
//}
