package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CreateEventRequest;
import com.trelix.trelix_app.dto.EventDTO;
import com.trelix.trelix_app.dto.UpdateEventRequest;
import com.trelix.trelix_app.entity.*;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.*;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.trelix.trelix_app.util.AppMapper.convertToEventDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AuthorizationService authService;

    public EventDTO createEvent(UUID teamId, UUID projectId, UUID taskId, CreateEventRequest eventRequest, UUID userId) {
        if (!authService.hasEventCreationAccess(teamId, projectId, taskId, userId)) {
            throw new AccessDeniedException("User doesn't has access to create events.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User couldn't be found."));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team couldn't be found."));
        Project project = null;
        Task task = null;
        if (projectId != null) project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project couldn't be found."));
        if (taskId != null) task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task couldn't be found."));
        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .description(eventRequest.getDescription())
                .startTime(eventRequest.getStartTime())
                .endTime(eventRequest.getEndTime())
                .createdBy(user)
                .team(team)
                .project(project)
                .task(task)
                .createdAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
        return convertToEventDTO(event);
    }

    public EventDTO getEvent(UUID userId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        UUID teamId = event.getTeam().getId();
        UUID projectId = event.getProject() != null ? event.getProject().getId() : null;
        UUID taskId = event.getTask() != null ? event.getTask().getId() : null;
        if (!authService.hasEventAccess(teamId, projectId, taskId, userId)) throw new AccessDeniedException("User doesn't have permission to access the event");
        return convertToEventDTO(event);
    }

    public List<EventDTO> getEvents(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (!authService.hasEventAccess(teamId, projectId, taskId, userId)) throw new AccessDeniedException("User doesn't have access to events");
        if (taskId != null) return eventRepository.findByTaskId(taskId).stream().map(AppMapper::convertToEventDTO).toList();
        if (projectId != null) return eventRepository.findByProjectId(projectId).stream().map(AppMapper::convertToEventDTO).toList();
        return eventRepository.findByTeamId(teamId).stream().map(AppMapper::convertToEventDTO).toList();
    }


    public EventDTO updateEvent(UUID eventId, UpdateEventRequest eventRequest, UUID userId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        UUID teamId = event.getTeam().getId();
        UUID projectId = event.getProject() != null ? event.getProject().getId() : null;
        UUID taskId = event.getTask() != null ? event.getTask().getId() : null;
        if (!authService.hasEventCreationAccess(teamId, projectId, taskId, userId)) {
            throw new AccessDeniedException("User doesn't has access to create events.");
        }
        if (eventRequest.getTitle() != null) event.setTitle(eventRequest.getTitle());
        if (eventRequest.getDescription() != null) event.setDescription(eventRequest.getDescription());
        if (eventRequest.getStartTime() != null) event.setStartTime(eventRequest.getStartTime());
        if (eventRequest.getEndTime() != null) event.setEndTime(eventRequest.getEndTime());
        eventRepository.save(event);
        return convertToEventDTO(event);
    }

    public void deleteEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        UUID teamId = event.getTeam().getId();
        UUID projectId = event.getProject() != null ? event.getProject().getId() : null;
        UUID taskId = event.getTask() != null ? event.getTask().getId() : null;
        if (!authService.hasEventCreationAccess(teamId, projectId, taskId, userId)) {
            throw new AccessDeniedException("User doesn't has access to delete events.");
        }
        eventRepository.deleteById(eventId);
    }


}
