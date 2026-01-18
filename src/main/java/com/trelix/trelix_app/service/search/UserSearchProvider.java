package com.trelix.trelix_app.service.search;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.SearchType;
import com.trelix.trelix_app.repository.UserRepository;
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
public class UserSearchProvider extends BaseSearchProvider {

    private final UserRepository userRepository;
    private static final int SEARCH_LIMIT = 50;

    @Override
    public SearchType getType() {
        return SearchType.USER;
    }

    @Override
    public List<SearchResultItem> search(String query, UUID userId) {
        log.debug("Searching users via Provider. Query: '{}'", query);
        Pageable limit = PageRequest.of(0, SEARCH_LIMIT);

        return userRepository.searchByNameOrEmail(query, limit)
                .stream()
                .map(user -> toSearchResultItem(user, query))
                .toList();
    }

    private SearchResultItem toSearchResultItem(User user, String query) {
        return new SearchResultItem(
                user.getId(),
                SearchType.USER.name(),
                user.getName(),
                user.getEmail(),
                generateSnippet(user.getName(), user.getEmail(), query),
                Map.of("email", user.getEmail()),
                user.getUpdatedAt(),
                calculateRelevance(user.getName(), user.getEmail(), query));
    }
}
