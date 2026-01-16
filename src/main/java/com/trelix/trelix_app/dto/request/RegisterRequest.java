package com.trelix.trelix_app.dto.request;

import com.trelix.trelix_app.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @Email @NotBlank String email,
        @StrongPassword @NotBlank String password
) {}




