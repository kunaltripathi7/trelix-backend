package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.validation.EndTimeAfterStartTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@EndTimeAfterStartTime(message = "End time must be after start time")
public record CreateEventRequest(
        @NotNull EventEntityType entityType,
        @NotNull UUID entityId,
        @NotBlank @Size(min = 3, max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @FutureOrPresent LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}
