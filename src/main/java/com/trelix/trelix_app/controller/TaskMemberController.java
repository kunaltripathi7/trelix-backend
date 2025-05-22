package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.service.TaskMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskMemberController {

    @Autowired
    private TaskMemberService taskMemberService;

    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/members")
    public ResponseEntity<List<TaskMemberDTO>> getTaskMembers(@PathVariable UUID teamId,
                                                               @PathVariable UUID projectId,
                                                               @PathVariable UUID taskId) {
        // Check if the user has access to the task
        authService.checkTaskAccess(teamId, projectId, taskId);

        // Fetch the task members
        List<TaskMemberDTO> taskMembers = taskMemberService.getTaskMembers(taskId);

        return ResponseEntity.ok(taskMembers);
    }
}
