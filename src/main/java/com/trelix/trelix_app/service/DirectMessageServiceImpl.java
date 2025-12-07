package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.DirectMessageConversationResponse;
import com.trelix.trelix_app.dto.DirectMessageDetailResponse;
import com.trelix.trelix_app.dto.DirectMessageMessageResponse;
import com.trelix.trelix_app.dto.DirectMessageResponse;
import com.trelix.trelix_app.dto.EditDirectMessageRequest;
import com.trelix.trelix_app.dto.PagedDirectMessageResponse;
import com.trelix.trelix_app.dto.SendDirectMessageRequest;
import com.trelix.trelix_app.entity.DirectMessage;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.DirectMessageRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final DirectMessageAuthorizationService dmAuthService;

    public DirectMessageServiceImpl(DirectMessageRepository directMessageRepository,
                                    MessageRepository messageRepository,
                                    UserService userService,
                                    DirectMessageAuthorizationService dmAuthService) {
        this.directMessageRepository = directMessageRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.dmAuthService = dmAuthService;
    }

    @Override
    @Transactional
    public DirectMessageResponse createOrGetDirectMessage(UUID currentUserId, UUID otherUserId) {
        // 1. Prevent self-messaging
        if (currentUserId.equals(otherUserId)) {
            throw new BadRequestException("Cannot create a direct message conversation with yourself.");
        }

        // 2. Apply canonical ordering for user IDs
        UUID user1 = currentUserId.compareTo(otherUserId) < 0 ? currentUserId : otherUserId;
        UUID user2 = currentUserId.compareTo(otherUserId) < 0 ? otherUserId : currentUserId;

        // 3. Check if conversation already exists
        Optional<DirectMessage> existingDm = directMessageRepository.findByUsers(user1, user2);

        if (existingDm.isPresent()) {
            // If exists, return the existing conversation
            DirectMessage dm = existingDm.get();
            String user1Name = userService.findById(dm.getUser1Id()).map(User::getUsername).orElse("Unknown User");
            String user2Name = userService.findById(dm.getUser2Id()).map(User::getUsername).orElse("Unknown User");
            return DirectMessageResponse.from(dm, user1Name, user2Name);
        } else {
            // 4. If not, create a new conversation
            DirectMessage newDm = DirectMessage.builder()
                    .user1Id(user1)
                    .user2Id(user2)
                    .createdAt(LocalDateTime.now())
                    .build();
            DirectMessage savedDm = directMessageRepository.save(newDm);

            String user1Name = userService.findById(savedDm.getUser1Id()).map(User::getUsername).orElse("Unknown User");
            String user2Name = userService.findById(savedDm.getUser2Id()).map(User::getUsername).orElse("Unknown User");
            return DirectMessageResponse.from(savedDm, user1Name, user2Name);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectMessageConversationResponse> getAllDirectMessages(UUID userId) {
        List<Object[]> results = directMessageRepository.findByUserIdWithLastMessage(userId);

        return results.stream()
                .map(obj -> {
                    DirectMessage dm = (DirectMessage) obj[0];
                    String lastMessageContent = (String) obj[1];
                    LocalDateTime lastMessageAt = (LocalDateTime) obj[2];

                    UUID otherUserId = dm.getUser1Id().equals(userId) ? dm.getUser2Id() : dm.getUser1Id();
                    User otherUser = userService.findById(otherUserId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + otherUserId));

                    // Truncate last message content for preview if needed
                    String previewContent = (lastMessageContent != null && lastMessageContent.length() > 100)
                            ? lastMessageContent.substring(0, 100) + "..."
                            : lastMessageContent;

                    return DirectMessageConversationResponse.from(
                            dm,
                            userId,
                            otherUser.getUsername(),
                            otherUser.getEmail(),
                            previewContent,
                            lastMessageAt
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DirectMessageDetailResponse getDirectMessageById(UUID dmId, UUID requesterId) {
        // 1. Verify requester is a participant
        dmAuthService.verifyParticipant(dmId, requesterId);

        // 2. Find the DirectMessage
        DirectMessage dm = directMessageRepository.findById(dmId)
                .orElseThrow(() -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        // 3. Fetch user details for both participants
        User user1 = userService.findById(dm.getUser1Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dm.getUser1Id()));
        User user2 = userService.findById(dm.getUser2Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dm.getUser2Id()));

        // 4. Map to DTO
        return DirectMessageDetailResponse.from(
                dm,
                user1.getUsername(),
                user1.getEmail(),
                user2.getUsername(),
                user2.getEmail()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedDirectMessageResponse getMessages(UUID dmId, int page, int size, UUID requesterId) {
        // 1. Verify requester is a participant
        dmAuthService.verifyParticipant(dmId, requesterId);

        // 2. Prepare Pageable for pagination and sorting (oldest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        // 3. Fetch messages
        Page<Message> messagePage = messageRepository.findByDirectMessageIdOrderByCreatedAtAsc(dmId, pageable);

        // 4. Map to PagedDirectMessageResponse DTO
        return PagedDirectMessageResponse.from(messagePage, userService);
    }

    @Override
    @Transactional
    public DirectMessageMessageResponse sendMessage(UUID dmId, SendDirectMessageRequest request, UUID senderId) {
        // 1. Verify sender is a participant
        dmAuthService.verifyParticipant(dmId, senderId);

        // 2. Ensure DM exists
        directMessageRepository.findById(dmId)
                .orElseThrow(() -> new ResourceNotFoundException("Direct message conversation not found with ID: " + dmId));

        // 3. Create Message entity
        Message message = Message.builder()
                .directMessageId(dmId)
                .channelId(null) // XOR constraint: directMessageId is set, channelId is null
                .senderId(senderId)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        // 4. Fetch sender name
        String senderName = userService.findById(senderId)
                .map(User::getUsername)
                .orElse("Unknown User");

        // 5. Map to DTO
        return DirectMessageMessageResponse.from(savedMessage, senderName);
    }

    @Override
    @Transactional
    public DirectMessageMessageResponse editMessage(UUID dmId, UUID messageId, EditDirectMessageRequest request, UUID requesterId) {
        // 1. Verify requester is a participant
        dmAuthService.verifyParticipant(dmId, requesterId);

        // 2. Find the message
        Message message = messageRepository.findByIdAndDirectMessageId(messageId, dmId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId + " in DM: " + dmId));

        // 3. Verify requester is the sender
        dmAuthService.verifyMessageSender(message, requesterId);

        // 4. Verify edit time window
        dmAuthService.verifyEditTimeWindow(message);

        // 5. Update message content and editedAt timestamp
        message.setContent(request.content());
        message.setEditedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);

        // 6. Fetch sender name
        String senderName = userService.findById(updatedMessage.getSenderId())
                .map(User::getUsername)
                .orElse("Unknown User");

        // 7. Map to DTO
        return DirectMessageMessageResponse.from(updatedMessage, senderName);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID dmId, UUID messageId, UUID requesterId) {
        // 1. Verify requester is a participant
        dmAuthService.verifyParticipant(dmId, requesterId);

        // 2. Find the message
        Message message = messageRepository.findByIdAndDirectMessageId(messageId, dmId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId + " in DM: " + dmId));

        // 3. Verify requester is the sender
        dmAuthService.verifyMessageSender(message, requesterId);

        // 4. Delete the message
        messageRepository.delete(message);
    }
}
