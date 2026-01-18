package com.trelix.trelix_app.service.search;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskSearchProvider extends BaseSearchProvider {

    private final TaskRepository taskRepository;
    private static final int SEARCH_LIMIT = 50;

    @Override
    public SearchType getType() {
        return SearchType.TASK;
    }

    @Override
    public List<SearchResultItem> search(String query, UUID userId) {
        log.debug("Searching tasks via Provider. Query: '{}', UserId: {}", query, userId);
        Pageable limit = PageRequest.of(0, SEARCH_LIMIT);

        return taskRepository.findTasksForUser(userId, null, null, null, null, query, limit)
                .stream()
                .map(task -> toSearchResultItem(task, query))
                .toList();
    }

    private SearchResultItem toSearchResultItem(Task task, String query) {
        return new SearchResultItem(
                task.getId(),
                SearchType.TASK.name(),
                task.getTitle(),
                task.getDescription(),
                generateSnippet(task.getTitle(), task.getDescription(), query),
                Map.of("projectId", task.getProject() != null ? task.getProject().getId().toString() : "N/A",
                        "projectName", task.getProject() != null ? task.getProject().getName() : "N/A",
                        "status", task.getStatus().name(),
                        "priority", task.getPriority().name()),
                task.getUpdatedAt(),
                calculateRelevance(task.getTitle(), task.getDescription(), query));
    }
}
