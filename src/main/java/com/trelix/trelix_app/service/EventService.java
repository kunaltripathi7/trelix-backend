package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.CreateEventRequest;
import com.trelix.trelix_app.dto.response.EventResponse;
import com.trelix.trelix_app.dto.response.PagedEventResponse;
import com.trelix.trelix_app.dto.request.UpdateEventRequest;
import com.trelix.trelix_app.enums.EventEntityType;

import java.time.LocalDate;
import java.util.UUID;

public interface EventService {
    EventResponse createEvent(CreateEventRequest request, UUID creatorId);

    PagedEventResponse getEvents(EventEntityType entityType, UUID entityId, LocalDate startDate, LocalDate endDate, int page, int size, UUID requesterId);

    EventResponse getEventById(UUID eventId, UUID requesterId);

    EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UUID requesterId);

    void deleteEvent(UUID eventId, UUID requesterId);
}




