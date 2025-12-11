//package com.trelix.trelix_app.controller;
//
//import com.trelix.trelix_app.dto.GlobalSearchResponse;
//import com.trelix.trelix_app.dto.ProjectSearchResponse;
//import com.trelix.trelix_app.dto.TaskSearchResponse;
//import com.trelix.trelix_app.dto.TeamSearchResponse;
//import com.trelix.trelix_app.dto.UserSearchResponse;
//import com.trelix.trelix_app.enums.SearchType;
//import com.trelix.trelix_app.enums.SortType;
//import com.trelix.trelix_app.enums.TaskStatus;
//import com.trelix.trelix_app.service.SearchService;
//import jakarta.validation.constraints.Length;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.UUID;
//
////@RestController
////@RequestMapping("/v1/search")
////@RequiredArgsConstructor
////@Validated
//public class SearchController {
//
//    private final SearchService searchService;
//
//    @GetMapping
//    public ResponseEntity<GlobalSearchResponse> globalSearch(
//            @RequestParam @Length(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
//            @RequestParam(required = false) SearchType type,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "RELEVANCE") SortType sort,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID userId = UUID.fromString(userDetails.getUsername());
//        GlobalSearchResponse response = searchService.globalSearch(query, type, page, size, sort, userId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/teams")
//    public ResponseEntity<Page<TeamSearchResponse>> searchTeams(
//            @RequestParam @Length(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID userId = UUID.fromString(userDetails.getUsername());
//        Page<TeamSearchResponse> response = searchService.searchTeams(query, page, size, userId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/projects")
//    public ResponseEntity<Page<ProjectSearchResponse>> searchProjects(
//            @RequestParam @Length(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
//            @RequestParam(required = false) UUID teamId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID userId = UUID.fromString(userDetails.getUsername());
//        Page<ProjectSearchResponse> response = searchService.searchProjects(query, teamId, page, size, userId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/tasks")
//    public ResponseEntity<Page<TaskSearchResponse>> searchTasks(
//            @RequestParam @Length(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
//            @RequestParam(required = false) UUID projectId,
//            @RequestParam(required = false) TaskStatus status,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID userId = UUID.fromString(userDetails.getUsername());
//        Page<TaskSearchResponse> response = searchService.searchTasks(query, projectId, status, page, size, userId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/users")
//    public ResponseEntity<Page<UserSearchResponse>> searchUsers(
//            @RequestParam @Length(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String query,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        Page<UserSearchResponse> response = searchService.searchUsers(query, page, size);
//        return ResponseEntity.ok(response);
//    }
//}
