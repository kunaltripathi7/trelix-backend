package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.ChannelRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddChannelMemberRequest(
        @NotNull(message = "User ID cannot be null")
        UUID userId,

        ChannelRole role // Optional, default MEMBER
) {}
