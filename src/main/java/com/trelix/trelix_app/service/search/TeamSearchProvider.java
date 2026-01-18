package com.trelix.trelix_app.service.search;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
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
public class TeamSearchProvider extends BaseSearchProvider {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private static final int SEARCH_LIMIT = 50;

    @Override
    public SearchType getType() {
        return SearchType.TEAM;
    }

    @Override
    public List<SearchResultItem> search(String query, UUID userId) {
        log.debug("Searching teams via Provider. Query: '{}', UserId: {}", query, userId);
        Pageable limit = PageRequest.of(0, SEARCH_LIMIT);

        return teamRepository.searchByUserAccess(query, userId, limit)
                .stream()
                .map(team -> toSearchResultItem(team, query))
                .toList();
    }

    private SearchResultItem toSearchResultItem(Team team, String query) {
        return new SearchResultItem(
                team.getId(),
                SearchType.TEAM.name(),
                team.getName(),
                team.getDescription(),
                generateSnippet(team.getName(), team.getDescription(), query),
                Map.of("memberCount", String.valueOf(teamUserRepository.countById_TeamId(team.getId()))),
                team.getUpdatedAt(),
                calculateRelevance(team.getName(), team.getDescription(), query));
    }
}
