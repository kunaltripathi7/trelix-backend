//package com.trelix.trelix_app.service;
//
//import com.trelix.trelix_app.dto.GlobalSearchResponse;
//import com.trelix.trelix_app.dto.ProjectSearchResponse;
//import com.trelix.trelix_app.dto.TaskSearchResponse;
//import com.trelix.trelix_app.dto.TeamSearchResponse;
//import com.trelix.trelix_app.dto.UserSearchResponse;
//import com.trelix.trelix_app.enums.SearchType;
//import com.trelix.trelix_app.enums.SortType;
//import com.trelix.trelix_app.enums.TaskStatus;
//import org.springframework.data.domain.Page;
//
//import java.util.UUID;
//
//public interface SearchService {
//    GlobalSearchResponse globalSearch(String query, SearchType type, int page, int size, SortType sort, UUID userId);
//
//    Page<TeamSearchResponse> searchTeams(String query, int page, int size, UUID userId);
//
//    Page<ProjectSearchResponse> searchProjects(String query, UUID teamId, int page, int size, UUID userId);
//
//    Page<TaskSearchResponse> searchTasks(String query, UUID projectId, TaskStatus status, int page, int size, UUID userId);
//
//    Page<UserSearchResponse> searchUsers(String query, int page, int size);
//}
