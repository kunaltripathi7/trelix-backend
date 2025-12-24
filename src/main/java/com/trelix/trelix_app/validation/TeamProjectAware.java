package com.trelix.trelix_app.validation;

import java.util.UUID;

public interface TeamProjectAware {
    UUID teamId();
    UUID projectId();
}