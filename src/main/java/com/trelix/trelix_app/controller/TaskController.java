package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskDTO;
import com.trelix.trelix_app.dto.TaskDetailsDTO;
import com.trelix.trelix_app.dto.TaskRequest;
import com.trelix.trelix_app.dto.TaskSearchCriteria;
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
@RequestMapping("/projects")
public class TaskController {
    

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthorizationService authService;


    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID projectId,
                                              @Valid @RequestBody TaskRequest taskRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDTO createdTask = taskService.createTask(taskRequest, projectId, userDetails.getId());
        return ResponseEntity.ok(createdTask);

    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasks(@PathVariable UUID projectId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId, userDetails.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDetailsDTO> getTask(@PathVariable UUID projectId,
                                                      @PathVariable UUID taskId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDetailsDTO task = taskService.getTaskById(taskId, projectId, userDetails.getId());
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID projectId,
                                              @PathVariable UUID taskId,
                                              @Valid @RequestBody TaskRequest taskRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskDTO updatedTask = taskService.updateTask(taskId, projectId, taskRequest, userDetails.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID projectId,
                                           @PathVariable UUID taskId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.deleteTask(taskId, projectId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/tasks/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@PathVariable UUID projectId,
                                                     @ModelAttribute TaskSearchCriteria taskSearchCriteria,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskDTO> tasks = taskService.searchTasks(projectId, taskSearchCriteria, userDetails.getId());
        return ResponseEntity.ok(tasks);
    }

}
