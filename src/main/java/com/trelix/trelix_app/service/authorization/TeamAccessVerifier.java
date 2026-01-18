package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.enums.EventEntityType;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamAccessVerifier implements EventEntityAccessVerifier {

    private final TeamUserRepository teamUserRepository;

    @Override
    public EventEntityType getType() {
        return EventEntityType.TEAM;
    }

    @Override
    public void verify(UUID entityId, UUID userId) {
        teamUserRepository.findById_TeamIdAndId_UserId(entityId, userId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this team."));
    }
}
