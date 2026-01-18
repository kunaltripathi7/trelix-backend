package com.trelix.trelix_app.dto.common;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record SearchResultItem(
                UUID id,
                String type, // TEAM/PROJECT/TASK/USER/CHANNEL/MESSAGE
                String title, // entity name/title
                String description,
                String snippet, // matching text excerpt with context
                Map<String, String> metadata, // entity-specific fields
                LocalDateTime lastUpdated,
                double relevanceScore // optional, for ranking
) {
}
