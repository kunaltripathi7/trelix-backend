package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferOwnershipRequest(
    @NotNull(message = "New owner ID cannot be null.")
    UUID newOwnerId
) {}
