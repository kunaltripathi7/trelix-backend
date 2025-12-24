package com.trelix.trelix_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.enums.TaskRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static List<TaskMemberResponse> from(List<TaskMember> taskMembers) {
        return taskMembers.stream()
                .map(TaskMemberResponse::from)
                .collect(Collectors.toList());
    }
}
