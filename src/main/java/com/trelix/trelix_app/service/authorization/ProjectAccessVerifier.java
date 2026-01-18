package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectAccessVerifier implements EventEntityAccessVerifier {

    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public EventEntityType getType() {
        return EventEntityType.PROJECT;
    }

    @Override
    public void verify(UUID entityId, UUID userId) {
        projectMemberRepository.findByIdProjectIdAndIdUserId(entityId, userId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this project."));
    }
}
