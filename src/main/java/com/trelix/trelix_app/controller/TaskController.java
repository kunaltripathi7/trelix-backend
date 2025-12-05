package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskDTO;
import com.trelix.trelix_app.dto.TaskDetailsDTO;
import com.trelix.trelix_app.dto.TaskRequest;
import com.trelix.trelix_app.dto.TaskSearchCriteria;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID projectId,
                                              @Valid @RequestBody TaskRequest taskRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDTO createdTask = taskService.createTask(taskRequest, projectId, userDetails.getId());
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable UUID projectId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId, userDetails.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDetailsDTO> getTask(@PathVariable UUID taskId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDetailsDTO task = taskService.getTaskById(taskId, userDetails.getId());
        return ResponseEntity.ok(task);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID taskId,
                                              @Valid @RequestBody TaskRequest taskRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDTO updatedTask = taskService.updateTask(taskId, taskRequest, userDetails.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.deleteTask(taskId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/tasks/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@PathVariable UUID projectId,
                                                     @ModelAttribute TaskSearchCriteria criteria,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDTO> tasks = taskService.searchTasks(projectId, criteria, userDetails.getId());
        return ResponseEntity.ok(tasks);
    }
}
