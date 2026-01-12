package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CreateEventRequest;
import com.trelix.trelix_app.dto.EventResponse;
import com.trelix.trelix_app.dto.PagedEventResponse;
import com.trelix.trelix_app.dto.UpdateEventRequest;
import com.trelix.trelix_app.entity.Event;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ForbiddenException;
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
    private final TeamService teamService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request, UUID creatorId) {
        verifyEntityAccess(request.entityType(), request.entityId(), creatorId);

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
        return toEventResponse(event, creator.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedEventResponse getEvents(EventEntityType entityType, UUID entityId, LocalDate startDate, LocalDate endDate, int page, int size, UUID requesterId) {
        if (entityType != null && entityId != null) {
            verifyEntityAccess(entityType, entityId, requesterId);
        } else if (entityType != null || entityId != null) {
            throw new BadRequestException("Both entityType and entityId must be provided if one is present.", ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<Event> eventPage = eventRepository.findByFilters(entityType, entityId, startDateTime, endDateTime, pageable);

        List<EventResponse> eventResponses = eventPage.getContent().stream()
                .map(event -> {
                    User creator = userRepository.findById(event.getCreatedBy()).orElse(null);
                    return toEventResponse(event, creator != null ? creator.getName() : "Unknown");
                })
                .collect(Collectors.toList());

        return new PagedEventResponse(
                eventResponses,
                eventPage.getNumber(),
                eventPage.getTotalPages(),
                eventPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        verifyEntityAccess(event.getEntityType(), event.getEntityId(), requesterId);

        User creator = userRepository.findById(event.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found for event"));

        return toEventResponse(event, creator.getName());
    }

    @Override
    @Transactional
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        verifyModifyPermission(event, requesterId);

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());

        event = eventRepository.save(event);
        User creator = userRepository.findById(event.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found for event"));
        return toEventResponse(event, creator.getName());
    }

    @Override
    @Transactional
    public void deleteEvent(UUID eventId, UUID requesterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        verifyModifyPermission(event, requesterId);

        eventRepository.delete(event);
    }

    private void verifyEntityAccess(EventEntityType entityType, UUID entityId, UUID userId) {
        try {
            switch (entityType) {
                case TEAM -> teamService.getTeamById(entityId, userId);
                case PROJECT -> projectService.getProjectById(entityId, userId);
                case TASK -> taskService.getTaskById(entityId, userId);
                default -> throw new BadRequestException("Unsupported entity type for events: " + entityType, ErrorCode.INVALID_INPUT);
            }
        } catch (ResourceNotFoundException e) {
            throw new ForbiddenException("You do not have access to this entity.", ErrorCode.FORBIDDEN);
        }
    }

    private void verifyModifyPermission(Event event, UUID userId) {
        if (event.getCreatedBy().equals(userId)) {
            return;
        }
        throw new ForbiddenException("You do not have permission to modify this event.", ErrorCode.FORBIDDEN);
    }

    private EventResponse toEventResponse(Event event, String creatorName) {
        String entityName = getEntityName(event.getEntityType(), event.getEntityId());

        return new EventResponse(
                event.getId(),
                event.getEntityType(),
                event.getEntityId(),
                entityName,
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCreatedBy(),
                creatorName,
                event.getCreatedAt()
        );
    }

    private String getEntityName(EventEntityType entityType, UUID entityId) {
        return entityType.name() + " " + entityId.toString().substring(0, 8);
    }
}
