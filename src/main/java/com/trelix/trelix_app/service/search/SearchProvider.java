package com.trelix.trelix_app.service.search;

import com.trelix.trelix_app.dto.common.SearchResultItem;
import com.trelix.trelix_app.enums.SearchType;

import java.util.List;
import java.util.UUID;

public interface SearchProvider {
    SearchType getType();

    List<SearchResultItem> search(String query, UUID userId);
}
