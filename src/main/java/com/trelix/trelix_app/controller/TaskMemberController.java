package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.TaskMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskMemberController {

    @Autowired
    private TaskMemberService taskMemberService;

    @Autowired
    private AuthorizationService authService;



    @GetMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/members")
    public ResponseEntity<List<MemberDTO>> getTaskMembers(@PathVariable UUID teamId,
                                                          @PathVariable UUID projectId,
                                                          @PathVariable UUID taskId,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        List<MemberDTO> taskMembers = taskMemberService.getTaskMembers(taskId);
        return ResponseEntity.ok(taskMembers);
    }

    @PostMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/members")
    public ResponseEntity<String> assignUserToTask(@PathVariable UUID teamId,
                                                          @PathVariable UUID projectId,
                                                          @PathVariable UUID taskId,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        taskMemberService.assignUserToTask(taskId, userDetails.getId());
        return ResponseEntity.ok("User assigned to task successfully");
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}/tasks/{taskId}/members/{userId}")
    public ResponseEntity<String> removeUserFromTask(@PathVariable UUID teamId,
                                                     @PathVariable UUID projectId,
                                                     @PathVariable UUID taskId,
                                                     @PathVariable UUID userId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.checkTaskAccess(teamId, projectId, taskId, userDetails.getId());
        taskMemberService.removeUserFromTask(taskId, userId);
        return ResponseEntity.ok("User removed from task successfully");
    }
}
