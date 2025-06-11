package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskDTO;
import com.trelix.trelix_app.dto.TaskDetailsDTO;
import com.trelix.trelix_app.dto.TaskRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {
    

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthorizationService authService;


    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID teamId,
                                              @PathVariable UUID projectId,
                                              @Valid @RequestBody TaskRequest taskRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskDTO createdTask = taskService.createTask(taskRequest, projectId);
        return ResponseEntity.ok(createdTask);

    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasks(@PathVariable UUID teamId,
                                                  @PathVariable UUID projectId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDetailsDTO> getTask(@PathVariable UUID teamId,
                                                      @PathVariable UUID projectId,
                                                      @PathVariable UUID taskId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskDetailsDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID teamId,
                                              @PathVariable UUID projectId,
                                              @PathVariable UUID taskId,
                                              @Valid @RequestBody TaskRequest taskRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        TaskDTO updatedTask = taskService.updateTask(taskId, taskRequest, userDetails.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID teamId,
                                           @PathVariable UUID projectId,
                                           @PathVariable UUID taskId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamId, projectId, userDetails.getId());
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
