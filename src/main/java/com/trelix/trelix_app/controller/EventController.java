package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.CreateEventRequest;
import com.trelix.trelix_app.dto.EventDTO;
import com.trelix.trelix_app.dto.UpdateEventRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthorizationService authService;

    @PostMapping("/events")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventRequest eventRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID teamId = UUID.fromString(eventRequest.getTeamId());
        UUID projectId = eventRequest.getProjectId() != null ? UUID.fromString(eventRequest.getProjectId()) : null;
        UUID taskId = eventRequest.getTaskId() != null ? UUID.fromString(eventRequest.getTaskId()) : null;
        EventDTO createdEvent = eventService.createEvent(teamId, projectId, taskId, eventRequest, userDetails.getId());
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> getEvent(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID eventId) {
        EventDTO event = eventService.getEvent(userDetails.getId(), eventId);
        return ResponseEntity.ok(event);
    }


    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getEvents(@RequestParam UUID teamId,
        @RequestParam(required = false) UUID projectId,
                @RequestParam(required = false) UUID taskId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<EventDTO> events = eventService.getEvents(teamId, projectId, taskId, userDetails.getId());
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable UUID eventId, @Valid @RequestBody UpdateEventRequest eventRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventDTO updatedEvent =  eventService.updateEvent(eventId, eventRequest, userDetails.getId());
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable UUID eventId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        eventService.deleteEvent(eventId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
