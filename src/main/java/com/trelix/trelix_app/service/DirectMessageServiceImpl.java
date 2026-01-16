package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.response.DirectMessageConversationResponse;
import com.trelix.trelix_app.dto.response.DirectMessageDetailResponse;
import com.trelix.trelix_app.dto.response.DirectMessageMessageResponse;
import com.trelix.trelix_app.dto.response.DirectMessageResponse;
import com.trelix.trelix_app.dto.request.EditDirectMessageRequest;
import com.trelix.trelix_app.dto.response.PagedDirectMessageResponse;
import com.trelix.trelix_app.dto.request.SendDirectMessageRequest;
import com.trelix.trelix_app.entity.DirectMessage;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.DirectMessageRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Override
    @Transactional
    public DirectMessageResponse createOrGetDirectMessage(UUID currentUserId, UUID otherUserId) {
        // 1. Prevent self-messaging
        if (currentUserId.equals(otherUserId)) {
            throw new BadRequestException("Cannot create a direct message conversation with yourself.",
                    ErrorCode.INVALID_INPUT);
        }

        // 2. Apply canonical ordering for user IDs
        UUID user1 = currentUserId.compareTo(otherUserId) < 0 ? currentUserId : otherUserId;
        UUID user2 = currentUserId.compareTo(otherUserId) < 0 ? otherUserId : currentUserId;

        // 3. Check if conversation already exists
        Optional<DirectMessage> existingDm = directMessageRepository.findByUsers(user1, user2);

        if (existingDm.isPresent()) {
            // If exists, return the existing conversation
            DirectMessage dm = existingDm.get();
            String user1Name = dm.getUser1().getName();
            String user2Name = dm.getUser2().getName();
            return DirectMessageResponse.from(dm, user1Name, user2Name);
        } else {
            // 4. If not, create a new conversation
            User user1Entity = userService.findById(user1);
            User user2Entity = userService.findById(user2);

            DirectMessage newDm = DirectMessage.builder()
                    .user1(user1Entity)
                    .user2(user2Entity)
                    .build();
            DirectMessage savedDm = directMessageRepository.save(newDm);

            return DirectMessageResponse.from(savedDm, savedDm.getUser1().getName(), savedDm.getUser2().getName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectMessageConversationResponse> getAllDirectMessages(UUID userId) {
        List<Object[]> results = directMessageRepository.findByUserIdWithLastMessage(userId);

        return results.stream()
                .map(obj -> {
                    DirectMessage dm = (DirectMessage) obj[0];
                    String lastMessageContent = obj[1] != null ? (String) obj[1] : null;
                    LocalDateTime lastMessageAt = obj[2] != null ? (LocalDateTime) obj[2] : null;

                    UUID otherUserId = dm.getUser1().getId().equals(userId) ? dm.getUser2().getId()
                            : dm.getUser1().getId();
                    User otherUser = userService.findById(otherUserId);

                    // Truncate last message content for preview if needed
                    String previewContent = (lastMessageContent != null && lastMessageContent.length() > 100)
                            ? lastMessageContent.substring(0, 100) + "..."
                            : lastMessageContent;

                    return DirectMessageConversationResponse.from(
                            dm,
                            userId,
                            otherUser.getName(),
                            otherUser.getEmail(),
                            previewContent,
                            lastMessageAt);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DirectMessageDetailResponse getDirectMessageById(UUID dmId, UUID requesterId) {
        // 1. Find the DirectMessage
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        // 2. Verify requester is a participant
        if (!dm.getUser1().getId().equals(requesterId) && !dm.getUser2().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not a participant in this conversation.", ErrorCode.FORBIDDEN);
        }

        // 3. Map to DTO
        return DirectMessageDetailResponse.from(
                dm,
                dm.getUser1().getName(),
                dm.getUser1().getEmail(),
                dm.getUser2().getName(),
                dm.getUser2().getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedDirectMessageResponse getMessages(UUID dmId, int page, int size, UUID requesterId) {
        // 1. Verify requester is a participant
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        if (!dm.getUser1().getId().equals(requesterId) && !dm.getUser2().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not a participant in this conversation.", ErrorCode.FORBIDDEN);
        }

        // 2. Prepare Pageable for pagination and sorting (oldest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        // 3. Fetch messages
        Page<Message> messagePage = messageRepository.findByDirectMessageIdOrderByCreatedAtAsc(dmId, pageable);

        // 4. Map to PagedDirectMessageResponse DTO
        return PagedDirectMessageResponse.from(messagePage);
    }

    @Override
    @Transactional
    public DirectMessageMessageResponse sendMessage(UUID dmId, SendDirectMessageRequest request, UUID senderId) {
        // 1. Verify sender is a participant
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        if (!dm.getUser1().getId().equals(senderId) && !dm.getUser2().getId().equals(senderId)) {
            throw new ForbiddenException("You are not a participant in this conversation.", ErrorCode.FORBIDDEN);
        }

        // 2. Fetch sender User entity
        User sender = userService.findById(senderId);

        // 3. Create Message entity
        Message message = Message.builder()
                .directMessage(dm)
                .sender(sender)
                .content(request.content())
                .build();

        Message savedMessage = messageRepository.save(message);

        // 4. Map to DTO
        return DirectMessageMessageResponse.from(savedMessage, sender.getName());
    }

    @Override
    @Transactional
    public DirectMessageMessageResponse editMessage(UUID dmId, UUID messageId, EditDirectMessageRequest request,
            UUID requesterId) {
        // 1. Verify requester is a participant
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        if (!dm.getUser1().getId().equals(requesterId) && !dm.getUser2().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not a participant in this conversation.", ErrorCode.FORBIDDEN);
        }

        // 2. Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (message.getDirectMessage() == null || !message.getDirectMessage().getId().equals(dmId)) {
            throw new ResourceNotFoundException("Message not found in this conversation.");
        }

        // 3. Verify requester is the sender
        if (!message.getSender().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not authorized to edit this message.", ErrorCode.FORBIDDEN);
        }

        // 4. Verify edit time window (15 minutes)
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(message.getCreatedAt(), now);
        if (duration.toMinutes() > 15) {
            throw new ForbiddenException("Message can only be edited within 15 minutes of sending.",
                    ErrorCode.FORBIDDEN);
        }

        // 5. Update message content and editedAt timestamp
        message.setContent(request.content());
        message.setEditedAt(now);

        Message updatedMessage = messageRepository.save(message);

        // 6. Map to DTO
        return DirectMessageMessageResponse.from(updatedMessage, updatedMessage.getSender().getName());
    }

    @Override
    @Transactional
    public void deleteMessage(UUID dmId, UUID messageId, UUID requesterId) {
        // 1. Verify requester is a participant
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        if (!dm.getUser1().getId().equals(requesterId) && !dm.getUser2().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not a participant in this conversation.", ErrorCode.FORBIDDEN);
        }

        // 2. Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (message.getDirectMessage() == null || !message.getDirectMessage().getId().equals(dmId)) {
            throw new ResourceNotFoundException("Message not found in this conversation.");
        }

        // 3. Verify requester is the sender
        if (!message.getSender().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not authorized to delete this message.", ErrorCode.FORBIDDEN);
        }

        // 4. Delete the message
        messageRepository.delete(message);
    }
}




