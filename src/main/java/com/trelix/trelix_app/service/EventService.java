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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public EventDTO createEvent(CreateEventRequest eventRequest, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Team team = teamRepository.findById(UUID.fromString(eventRequest.getTeamId()))
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + eventRequest.getTeamId()));

        Project project = null;
        if (eventRequest.getProjectId() != null) {
            project = projectRepository.findById(UUID.fromString(eventRequest.getProjectId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + eventRequest.getProjectId()));
        }

        Task task = null;
        if (eventRequest.getTaskId() != null) {
            task = taskRepository.findById(UUID.fromString(eventRequest.getTaskId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + eventRequest.getTaskId()));
        }

        authService.checkEventCreationAccess(team, project, task, userId);

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
        return AppMapper.convertToEventDTO(event);
    }

    public EventDTO getEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        authService.checkEventAccess(event, userId);
        return AppMapper.convertToEventDTO(event);
    }

    public List<EventDTO> getEvents(UUID teamId, UUID projectId, UUID taskId, UUID userId) {
        if (taskId != null) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
            authService.checkTaskAccessByTaskId(taskId, userId);
            return eventRepository.findByTaskId(taskId).stream().map(AppMapper::convertToEventDTO).toList();
        }
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
            authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);
            return eventRepository.findByProjectId(projectId).stream().map(AppMapper::convertToEventDTO).toList();
        }
        authService.checkTeamAccess(teamId, userId);
        return eventRepository.findByTeamId(teamId).stream().map(AppMapper::convertToEventDTO).toList();
    }

    public EventDTO updateEvent(UUID eventId, UpdateEventRequest eventRequest, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        authService.checkEventCreationAccess(event.getTeam(), event.getProject(), event.getTask(), userId);

        if (eventRequest.getTitle() != null) event.setTitle(eventRequest.getTitle());
        if (eventRequest.getDescription() != null) event.setDescription(eventRequest.getDescription());
        if (eventRequest.getStartTime() != null) event.setStartTime(eventRequest.getStartTime());
        if (eventRequest.getEndTime() != null) event.setEndTime(eventRequest.getEndTime());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        return AppMapper.convertToEventDTO(event);
    }

    public void deleteEvent(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        authService.checkEventCreationAccess(event.getTeam(), event.getProject(), event.getTask(), userId);
        eventRepository.deleteById(eventId);
    }
}
