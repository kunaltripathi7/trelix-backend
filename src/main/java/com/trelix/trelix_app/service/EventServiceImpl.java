package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.CreateEventRequest;
import com.trelix.trelix_app.dto.response.EventResponse;
import com.trelix.trelix_app.dto.response.PagedEventResponse;
import com.trelix.trelix_app.dto.request.UpdateEventRequest;
import com.trelix.trelix_app.entity.Event;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.EventRepository;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request, UUID creatorId) {
        authorizationService.verifyEntityAccess(request.entityType(), request.entityId(), creatorId);

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with ID: " + creatorId));

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .createdBy(creatorId)
                .entityType(request.entityType())
                .entityId(request.entityId())
                .build();

        event = eventRepository.save(event);
        String entityName = getEntityName(event.getEntityType(), event.getEntityId());
        return EventResponse.from(event, creator.getName(), entityName);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedEventResponse getEvents(EventEntityType entityType, UUID entityId, LocalDate startDate,
            LocalDate endDate, int page, int size, UUID requesterId) {
        if (entityType != null && entityId != null) {
            authorizationService.verifyEntityAccess(entityType, entityId, requesterId);
        } else if (entityType != null || entityId != null) {
            throw new BadRequestException("Both entityType and entityId must be provided if one is present.",
                    ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<Event> eventPage = eventRepository.findByFilters(entityType, entityId, startDateTime, endDateTime,
                pageable);

        List<EventResponse> eventResponses = eventPage.getContent().stream()
                .map(event -> {
                    User creator = userRepository.findById(event.getCreatedBy()).orElse(null);
                    String entityName = getEntityName(event.getEntityType(), event.getEntityId());
                    return EventResponse.from(event, creator != null ? creator.getName() : "Unknown", entityName);
                })
                .collect(Collectors.toList());

        return PagedEventResponse.from(eventPage, eventResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        authorizationService.verifyEntityAccess(event.getEntityType(), event.getEntityId(), requesterId);

        User creator = userRepository.findById(event.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found for event"));

        String entityName = getEntityName(event.getEntityType(), event.getEntityId());
        return EventResponse.from(event, creator.getName(), entityName);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        authorizationService.verifyEventModification(event, requesterId);

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());

        event = eventRepository.save(event);
        User creator = userRepository.findById(event.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found for event"));
        String entityName = getEntityName(event.getEntityType(), event.getEntityId());
        return EventResponse.from(event, creator.getName(), entityName);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID eventId, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        authorizationService.verifyEventModification(event, requesterId);

        eventRepository.delete(event);
    }

    private String getEntityName(EventEntityType entityType, UUID entityId) {
        return entityType.name() + " " + entityId.toString().substring(0, 8);
    }
}
