package com.trelix.trelix_app.dto.response;

import java.util.List;

public record PagedEventResponse(
                List<EventResponse> events,
                int currentPage,
                int totalPages,
                long totalElements) {
        public static PagedEventResponse from(org.springframework.data.domain.Page<?> page,
                        List<EventResponse> events) {
                return new PagedEventResponse(
                                events,
                                page.getNumber(),
                                page.getTotalPages(),
                                page.getTotalElements());
        }
}
