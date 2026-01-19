package com.trelix.trelix_app.validation;

import java.util.UUID;

// Generalized any can implement
public interface TeamProjectAware {
    UUID teamId();

    UUID projectId();
}