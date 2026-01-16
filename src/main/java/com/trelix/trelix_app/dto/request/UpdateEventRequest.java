package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.validation.EndTimeAfterStartTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@EndTimeAfterStartTime(message = "End time must be after start time")
public record UpdateEventRequest(
        @NotBlank @Size(min = 3, max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @FutureOrPresent LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}




