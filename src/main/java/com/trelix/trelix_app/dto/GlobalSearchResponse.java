package com.trelix.trelix_app.dto;

import java.util.List;

public record GlobalSearchResponse(
        List<SearchResultItem> results,
        int currentPage,
        int totalPages,
        long totalElements,
        String query,
        String appliedType
) {}
