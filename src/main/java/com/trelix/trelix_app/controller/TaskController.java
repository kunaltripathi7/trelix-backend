package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskDTO;
import com.trelix.trelix_app.dto.TaskRequest;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthorizationService authService;

//    POST /teams/{teamId}/projects/{projectId}/tasks — Create a new task in a project
//    GET /teams/{teamId}/projects/{projectId}/tasks — List all tasks in a project
//    GET /teams/{teamId}/projects/{projectId}/tasks/{taskId} — Get details of a specific task
//    PUT /teams/{teamId}/projects/{projectId}/tasks/{taskId} — Update a specific task
//    DELETE /teams/{teamId}/projects/{projectId}/tasks/{taskId} — Delete a specific task

    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID teamId,
                                              @PathVariable UUID projectId,
                                              TaskRequest taskRequest, CustomUserDetails userDetails) {
        authService.check
        TaskDTO createdTask = taskRepository.save(taskDTO);
        return ResponseEntity.ok(createdTask);

    }

}
