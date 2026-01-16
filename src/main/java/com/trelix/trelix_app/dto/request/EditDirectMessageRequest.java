package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditDirectMessageRequest(
        @NotBlank(message = "Message content cannot be empty")
        @Size(min = 1, max = 5000, message = "Message content must be between 1 and 5000 characters")
        String content
) {}




