package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageAttachmentAccessVerifier implements AttachmentEntityAccessVerifier {

    private final MessageRepository messageRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    public EntityType getType() {
        return EntityType.MESSAGE;
    }

    @Override
    public void verify(UUID entityId, UUID userId) {
        Message message = messageRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + entityId));

        if (message.getChannel() != null) {
            teamUserRepository.findById_TeamIdAndId_UserId(message.getChannel().getTeamId(), userId)
                    .orElseThrow(() -> new AccessDeniedException("You do not have access to this message."));
        }
    }
}
