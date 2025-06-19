package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskSearchCriteria {
    private UUID assigneeId;
    private String status;
    private Integer priority; // using Integer to allow null values
    private LocalDate dueBefore;
    private String search;
}
