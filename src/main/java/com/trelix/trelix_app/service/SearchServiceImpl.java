package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.GlobalSearchResponse;
import com.trelix.trelix_app.dto.ProjectSearchResponse;
import com.trelix.trelix_app.dto.SearchResultItem;
import com.trelix.trelix_app.dto.TaskSearchResponse;
import com.trelix.trelix_app.dto.TeamSearchResponse;
import com.trelix.trelix_app.dto.UserSearchResponse;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.enums.SortType;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    @Transactional(readOnly = true)
    public GlobalSearchResponse globalSearch(String query, SearchType type, int page, int size, SortType sort,
            UUID userId) {
        validateQuery(query);

        List<SearchResultItem> allResults = new ArrayList<>();
        Pageable unpaged = Pageable.unpaged();

        if (type == null || type == SearchType.TEAM) {
            allResults.addAll(teamRepository.searchByUserAccess(query, userId, unpaged).stream()
                    .map(team -> toSearchResultItem(team, query)).toList());
        }
        if (type == null || type == SearchType.PROJECT) {
            allResults.addAll(projectRepository.searchByUserAccess(query, null, userId, unpaged).stream()
                    .map(project -> toSearchResultItem(project, query)).toList());
        }
        if (type == null || type == SearchType.TASK) {
            allResults.addAll(taskRepository.findTasksForUser(userId, null, null, null, null, query, unpaged).stream()
                    .map(task -> toSearchResultItem(task, query)).toList());
        }
        if (type == null || type == SearchType.USER) {
            allResults.addAll(userRepository.searchByNameOrEmail(query, unpaged).stream()
                    .map(user -> toSearchResultItem(user, query)).toList());
        }

        if (sort == SortType.RELEVANCE) {
            allResults.sort(Comparator.comparingDouble(SearchResultItem::relevanceScore).reversed());
        } else {
            allResults.sort(Comparator.comparing(SearchResultItem::lastUpdated,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }

        int start = page * size;
        int end = Math.min(start + size, allResults.size());
        List<SearchResultItem> paginatedResults = allResults.subList(start, end);

        return new GlobalSearchResponse(paginatedResults, page, (int) Math.ceil((double) allResults.size() / size),
                allResults.size(), query, type != null ? type.toString() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamSearchResponse> searchTeams(String query, int page, int size, UUID userId) {
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Team> teamPage = teamRepository.searchByUserAccess(query, userId, pageable);
        return teamPage
                .map(team -> TeamSearchResponse.from(team, (int) teamUserRepository.countById_TeamId(team.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectSearchResponse> searchProjects(String query, UUID teamId, int page, int size, UUID userId) {
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Project> projectPage = projectRepository.searchByUserAccess(query, teamId, userId, pageable);
        return projectPage.map(project -> ProjectSearchResponse.from(project, project.getTeam().getName(),
                project.getTasks() != null ? project.getTasks().size() : 0));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskSearchResponse> searchTasks(String query, UUID projectId, TaskStatus status, int page, int size,
            UUID userId) {
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Task> taskPage = taskRepository.findTasksForUser(userId, null, projectId, status, null, query, pageable);
        return taskPage.map(TaskSearchResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSearchResponse> searchUsers(String query, int page, int size) {
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> userPage = userRepository.searchByNameOrEmail(query, pageable);
        return userPage.map(UserSearchResponse::from);
    }

    private void validateQuery(String query) {
        if (query == null || query.trim().length() < 2) {
            throw new BadRequestException("Search query must be at least 2 characters", ErrorCode.INVALID_INPUT);
        }
    }

    private SearchResultItem toSearchResultItem(Object entity, String query) {
        if (entity instanceof Team team) {
            return new SearchResultItem(team.getId(), SearchType.TEAM.name(), team.getName(), team.getDescription(),
                    generateSnippet(team.getName(), team.getDescription(), query),
                    Map.of("memberCount", String.valueOf(teamUserRepository.countById_TeamId(team.getId()))),
                    team.getUpdatedAt(), calculateRelevance(team.getName(), team.getDescription(), query));
        } else if (entity instanceof Project project) {
            return new SearchResultItem(project.getId(), SearchType.PROJECT.name(), project.getName(),
                    project.getDescription(), generateSnippet(project.getName(), project.getDescription(), query),
                    Map.of("teamId", project.getTeam().getId().toString(), "teamName", project.getTeam().getName(),
                            "taskCount", String.valueOf(project.getTasks() != null ? project.getTasks().size() : 0)),
                    project.getUpdatedAt(), calculateRelevance(project.getName(), project.getDescription(), query));
        } else if (entity instanceof Task task) {
            return new SearchResultItem(task.getId(), SearchType.TASK.name(), task.getTitle(), task.getDescription(),
                    generateSnippet(task.getTitle(), task.getDescription(), query),
                    Map.of("projectId", task.getProject() != null ? task.getProject().getId().toString() : "N/A",
                            "projectName", task.getProject() != null ? task.getProject().getName() : "N/A", "status",
                            task.getStatus().name(), "priority", task.getPriority().name()),
                    task.getUpdatedAt(), calculateRelevance(task.getTitle(), task.getDescription(), query));
        } else if (entity instanceof User user) {
            return new SearchResultItem(user.getId(), SearchType.USER.name(), user.getName(), user.getEmail(),
                    generateSnippet(user.getName(), user.getEmail(), query), Map.of("email", user.getEmail()),
                    user.getUpdatedAt(), calculateRelevance(user.getName(), user.getEmail(), query));
        }
        return null;
    }

    private double calculateRelevance(String title, String description, String query) {
        double score = 0.0;
        String lowerQuery = query.toLowerCase();
        String lowerTitle = title != null ? title.toLowerCase() : "";
        String lowerDesc = description != null ? description.toLowerCase() : "";
        if (lowerTitle.equals(lowerQuery))
            score += 100.0;
        else if (lowerTitle.startsWith(lowerQuery))
            score += 50.0;
        else if (lowerTitle.contains(lowerQuery))
            score += 25.0;
        if (lowerDesc.contains(lowerQuery))
            score += 10.0;
        score += (100.0 / (title != null ? title.length() + 1 : 1));
        return score;
    }

    private String generateSnippet(String title, String description, String query) {
        String text = description != null ? description : title;
        if (text == null || text.isEmpty())
            return "";
        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int index = lowerText.indexOf(lowerQuery);
        if (index == -1)
            return text.length() > 100 ? text.substring(0, 100) + "..." : text;
        int start = Math.max(0, index - 50);
        int end = Math.min(text.length(), index + query.length() + 50);
        String snippet = text.substring(start, end);
        if (start > 0)
            snippet = "..." + snippet;
        if (end < text.length())
            snippet = snippet + "...";
        return snippet;
    }
}
