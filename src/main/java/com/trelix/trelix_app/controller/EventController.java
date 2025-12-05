package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.CreateEventRequest;
import com.trelix.trelix_app.dto.EventDTO;
import com.trelix.trelix_app.dto.UpdateEventRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventRequest eventRequest,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventDTO createdEvent = eventService.createEvent(eventRequest, userDetails.getId());
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID eventId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventDTO event = eventService.getEvent(eventId, userDetails.getId());
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(@RequestParam(required = false) UUID teamId,
                                                    @RequestParam(required = false) UUID projectId,
                                                    @RequestParam(required = false) UUID taskId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<EventDTO> events = eventService.getEvents(teamId, projectId, taskId, userDetails.getId());
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable UUID eventId,
                                                @Valid @RequestBody UpdateEventRequest eventRequest,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventDTO updatedEvent = eventService.updateEvent(eventId, eventRequest, userDetails.getId());
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        eventService.deleteEvent(eventId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
