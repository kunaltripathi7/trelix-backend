package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.enums.TaskRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskMemberResponse(
        UUID userId,
        String userName,
        String email,
        TaskRole role,
        LocalDateTime assignedAt
) {
    public static TaskMemberResponse from(TaskMember taskMember) {
        return new TaskMemberResponse(
                taskMember.getUser().getId(),
                taskMember.getUser().getName(),
                taskMember.getUser().getEmail(),
                taskMember.getRole(),
                taskMember.getCreatedAt()
        );
    }
}
