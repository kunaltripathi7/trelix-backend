//package com.trelix.trelix_app.controller;
//
//import com.trelix.trelix_app.dto.CreateEventRequest;
//import com.trelix.trelix_app.dto.EventResponse;
//import com.trelix.trelix_app.dto.PagedEventResponse;
//import com.trelix.trelix_app.dto.UpdateEventRequest;
//import com.trelix.trelix_app.enums.EventEntityType;
//import com.trelix.trelix_app.service.EventService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import java.net.URI;
//import java.time.LocalDate;
//import java.util.UUID;
//
////@RestController
////@RequestMapping("/v1/events")
////@RequiredArgsConstructor
////@Validated
//public class EventController {
//
//    private final EventService eventService;
//
//    @PostMapping
//    public ResponseEntity<EventResponse> createEvent(
//            @Valid @RequestBody CreateEventRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID creatorId = UUID.fromString(userDetails.getUsername());
//        EventResponse response = eventService.createEvent(request, creatorId);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(response.id())
//                .toUri();
//
//        return ResponseEntity.created(location).body(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<PagedEventResponse> getEvents(
//            @RequestParam(required = false) EventEntityType entityType,
//            @RequestParam(required = false) UUID entityId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID requesterId = UUID.fromString(userDetails.getUsername());
//        PagedEventResponse response = eventService.getEvents(entityType, entityId, startDate, endDate, page, size, requesterId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{eventId}")
//    public ResponseEntity<EventResponse> getEventById(
//            @PathVariable UUID eventId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID requesterId = UUID.fromString(userDetails.getUsername());
//        EventResponse response = eventService.getEventById(eventId, requesterId);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{eventId}")
//    public ResponseEntity<EventResponse> updateEvent(
//            @PathVariable UUID eventId,
//            @Valid @RequestBody UpdateEventRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID requesterId = UUID.fromString(userDetails.getUsername());
//        EventResponse response = eventService.updateEvent(eventId, request, requesterId);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{eventId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> deleteEvent(
//            @PathVariable UUID eventId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        UUID requesterId = UUID.fromString(userDetails.getUsername());
//        eventService.deleteEvent(eventId, requesterId);
//        return ResponseEntity.noContent().build();
//    }
//}
