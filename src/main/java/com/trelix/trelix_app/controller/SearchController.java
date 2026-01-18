package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.response.GlobalSearchResponse;
import com.trelix.trelix_app.dto.response.ProjectSearchResponse;
import com.trelix.trelix_app.dto.response.TaskSearchResponse;
import com.trelix.trelix_app.dto.response.TeamSearchResponse;
import com.trelix.trelix_app.dto.response.UserSearchResponse;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.enums.SortType;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.SearchService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/search")
@Validated
@RequiredArgsConstructor
@Tag(name = "Search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Global search", description = "Search across teams, projects, tasks, and users")
    public ResponseEntity<GlobalSearchResponse> globalSearch(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
            @RequestParam(required = false) SearchType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RELEVANCE") SortType sort,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        GlobalSearchResponse response = searchService.globalSearch(query, type, page, size, sort, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams")
    @io.swagger.v3.oas.annotations.Operation(summary = "Search teams", description = "Search for teams the user is a member of")
    public ResponseEntity<Page<TeamSearchResponse>> searchTeams(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<TeamSearchResponse> response = searchService.searchTeams(query, page, size, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects")
    @io.swagger.v3.oas.annotations.Operation(summary = "Search projects", description = "Search for projects the user has access to")
    public ResponseEntity<Page<ProjectSearchResponse>> searchProjects(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<ProjectSearchResponse> response = searchService.searchProjects(query, teamId, page, size,
                currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks")
    @io.swagger.v3.oas.annotations.Operation(summary = "Search tasks", description = "Search for tasks assigned to or visible to the user")
    public ResponseEntity<Page<TaskSearchResponse>> searchTasks(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<TaskSearchResponse> response = searchService.searchTasks(query, projectId, status, page, size,
                currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @io.swagger.v3.oas.annotations.Operation(summary = "Search users", description = "Search for other users by name or email")
    public ResponseEntity<Page<UserSearchResponse>> searchUsers(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<UserSearchResponse> response = searchService.searchUsers(query, page, size);
        return ResponseEntity.ok(response);
    }
}
