package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.dto.response.GlobalSearchResponse;
import com.trelix.trelix_app.dto.response.ProjectSearchResponse;
import com.trelix.trelix_app.dto.response.TaskSearchResponse;
import com.trelix.trelix_app.dto.response.TeamSearchResponse;
import com.trelix.trelix_app.dto.response.UserSearchResponse;
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
import com.trelix.trelix_app.service.search.SearchProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final List<SearchProvider> searchProviders;

    @Override
    public GlobalSearchResponse globalSearch(String query, SearchType type, int page, int size, SortType sort,
            UUID userId) {
        log.info("Performing global search using Strategy Pattern. Query: '{}', Type: {}, UserId: {}", query, type,
                userId);
        validateQuery(query);

        List<SearchProvider> providersToUse = searchProviders.stream()
                .filter(p -> type == null || p.getType() == type)
                .toList();

        List<CompletableFuture<List<SearchResultItem>>> futures = providersToUse.stream()
                .map(provider -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return provider.search(query, userId);
                    } catch (Exception e) {
                        log.error("Provider {} failed: {}", provider.getType(), e.getMessage());
                        return new ArrayList<SearchResultItem>(); // Circuit breaker / graceful degradation
                    }
                }))
                .toList();

        List<SearchResultItem> allResults = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (sort == SortType.RELEVANCE) {
            allResults.sort(Comparator.comparingDouble(SearchResultItem::relevanceScore).reversed());
        } else {
            allResults.sort(Comparator.comparing(SearchResultItem::lastUpdated,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }

        int start = page * size;
        int end = Math.min(start + size, allResults.size());
        List<SearchResultItem> paginatedResults = (start > allResults.size()) ? new ArrayList<>()
                : allResults.subList(start, end);

        return new GlobalSearchResponse(paginatedResults, page, (int) Math.ceil((double) allResults.size() / size),
                allResults.size(), query, type != null ? type.toString() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamSearchResponse> searchTeams(String query, int page, int size, UUID userId) {
        log.debug("Searching teams. Query: '{}', UserId: {}", query, userId);
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Team> teamPage = teamRepository.searchByUserAccess(query, userId, pageable);
        return teamPage
                .map(team -> TeamSearchResponse.from(team, (int) teamUserRepository.countById_TeamId(team.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectSearchResponse> searchProjects(String query, UUID teamId, int page, int size, UUID userId) {
        log.debug("Searching projects. Query: '{}', TeamId: {}, UserId: {}", query, teamId, userId);
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
        log.debug("Searching tasks. Query: '{}', ProjectId: {}, Status: {}, UserId: {}", query, projectId, status,
                userId);
        validateQuery(query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Task> taskPage = taskRepository.findTasksForUser(userId, null, projectId, status, null, query, pageable);
        return taskPage.map(TaskSearchResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSearchResponse> searchUsers(String query, int page, int size) {
        log.debug("Searching users. Query: '{}'", query);
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
}
