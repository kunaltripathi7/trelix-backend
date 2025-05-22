package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.TaskStatusChangeDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TaskStatusChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TaskStatusChangeController {

    @Autowired
    private TaskStatusChangeService taskStatusChangeService;

    @Autowired
    private AuthorizationService authService;

    @GetMapping("/teams/{teamID}/projects/{projectID}/tasks/{taskID}/status-changes")
    public ResponseEntity<TaskStatusChangeDTO> getTaskStatusChanges(
            @PathVariable UUID teamID,
            @PathVariable UUID projectID,
            @PathVariable UUID taskID,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkProjectAccess(teamID, projectID, userDetails.getId());
        TaskStatusChangeDTO taskStatusChangeDTO = taskStatusChangeService.getChangesByTaskId(taskID);
        return ResponseEntity.ok(taskStatusChangeDTO);
    }
}
