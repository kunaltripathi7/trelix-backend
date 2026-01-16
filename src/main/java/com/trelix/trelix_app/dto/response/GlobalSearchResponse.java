package com.trelix.trelix_app.dto.response;

import java.util.List;

import com.trelix.trelix_app.dto.common.SearchResultItem;

public record GlobalSearchResponse(
                List<SearchResultItem> results,
                int currentPage,
                int totalPages,
                long totalElements,
                String query,
                String appliedType) {
}
