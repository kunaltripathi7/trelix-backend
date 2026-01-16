package com.trelix.trelix_app.dto.response;

import java.util.List;

public record PagedEventResponse(
        List<EventResponse> events,
        int currentPage,
        int totalPages,
        long totalElements
) {}




