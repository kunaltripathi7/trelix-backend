package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.TaskMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskMemberController {

    private final TaskMemberService taskMemberService;

    @GetMapping("/tasks/{taskId}/members")
    public ResponseEntity<List<MemberDTO>> getTaskMembers(@PathVariable UUID taskId,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MemberDTO> taskMembers = taskMemberService.getTaskMembers(taskId, userDetails.getId());
        return ResponseEntity.ok(taskMembers);
    }

    @PostMapping("/tasks/{taskId}/members/{userId}")
    public ResponseEntity<Void> assignUserToTask(@PathVariable UUID taskId,
                                                 @PathVariable UUID userId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskMemberService.assignUserToTask(taskId, userId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tasks/{taskId}/members/{userId}")
    public ResponseEntity<Void> removeUserFromTask(@PathVariable UUID taskId,
                                                     @PathVariable UUID userId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskMemberService.removeUserFromTask(taskId, userId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
