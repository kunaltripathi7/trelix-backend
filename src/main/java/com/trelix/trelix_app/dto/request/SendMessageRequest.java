package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SendMessageRequest(
        @NotNull(message = "Channel ID cannot be null")
        UUID channelId,

        @NotBlank(message = "Message content cannot be empty")
        @Size(min = 1, max = 5000, message = "Message content must be between 1 and 5000 characters")
        String content
) {}




