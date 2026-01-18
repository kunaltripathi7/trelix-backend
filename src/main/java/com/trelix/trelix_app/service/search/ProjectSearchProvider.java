package com.trelix.trelix_app.service.search;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.repository.ProjectRepository;
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
public class ProjectSearchProvider extends BaseSearchProvider {

    private final ProjectRepository projectRepository;
    private static final int SEARCH_LIMIT = 50;

    @Override
    public SearchType getType() {
        return SearchType.PROJECT;
    }

    @Override
    public List<SearchResultItem> search(String query, UUID userId) {
        log.debug("Searching projects via Provider. Query: '{}', UserId: {}", query, userId);
        Pageable limit = PageRequest.of(0, SEARCH_LIMIT);

        return projectRepository.searchByUserAccess(query, null, userId, limit)
                .stream()
                .map(project -> toSearchResultItem(project, query))
                .toList();
    }

    private SearchResultItem toSearchResultItem(Project project, String query) {
        return new SearchResultItem(
                project.getId(),
                SearchType.PROJECT.name(),
                project.getName(),
                project.getDescription(),
                generateSnippet(project.getName(), project.getDescription(), query),
                Map.of("teamId", project.getTeam().getId().toString(),
                        "teamName", project.getTeam().getName(),
                        "taskCount", String.valueOf(project.getTasks() != null ? project.getTasks().size() : 0)),
                project.getUpdatedAt(),
                calculateRelevance(project.getName(), project.getDescription(), query));
    }
}
