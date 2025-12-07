package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.EditMessageRequest;
import com.trelix.trelix_app.dto.MessageResponse;
import com.trelix.trelix_app.dto.PagedMessageResponse;
import com.trelix.trelix_app.dto.SendMessageRequest;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository; // To fetch Channel entity
import com.trelix.trelix_app.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository; // To fetch Channel entity for Message
    private final UserService userService; // To fetch User entity for Message sender
    private final ChannelService channelService; // To verify channel access
    private final ChannelAuthorizationService channelAuthorizationService; // For specific message auth

    public MessageServiceImpl(MessageRepository messageRepository,
                              ChannelRepository channelRepository,
                              UserService userService,
                              ChannelService channelService,
                              ChannelAuthorizationService channelAuthorizationService) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userService = userService;
        this.channelService = channelService;
        this.channelAuthorizationService = channelAuthorizationService;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, UUID senderId) {
        // 1. Verify sender has access to the channel
        channelService.verifyChannelAccess(request.channelId(), senderId);

        // 2. Fetch Channel entity
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + request.channelId()));

        // 3. Fetch Sender User entity
        User sender = userService.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found with ID: " + senderId));

        // 4. Create Message entity
        Message message = Message.builder()
                .channel(channel)
                .sender(sender)
                .content(request.content())
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResponse.from(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedMessageResponse getMessages(UUID channelId, int page, int size, UUID requesterId) {
        // 1. Verify requester has access to the channel
        channelService.verifyChannelAccess(channelId, requesterId);

        // 2. Prepare Pageable for pagination and sorting (oldest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        // 3. Fetch messages for the channel
        Page<Message> messagePage = messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId, pageable);

        // 4. Map to PagedMessageResponse DTO
        return PagedMessageResponse.from(messagePage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(UUID messageId, UUID requesterId) {
        // 1. Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        // 2. Verify requester has access to the message's channel
        channelService.verifyChannelAccess(message.getChannel().getId(), requesterId);

        // 3. Map to DTO
        return MessageResponse.from(message);
    }

    @Override
    @Transactional
    public MessageResponse editMessage(UUID messageId, EditMessageRequest request, UUID requesterId) {
        // 1. Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        // 2. Verify requester is the sender of the message
        if (!message.getSender().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not authorized to edit this message.");
        }

        // 3. Check if within 15-minute editing window
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(message.getCreatedAt(), now);
        if (duration.toMinutes() > 15) {
            throw new ForbiddenException("Message can only be edited within 15 minutes of sending.");
        }

        // 4. Update content and editedAt timestamp
        message.setContent(request.content());
        message.setEditedAt(now);

        // 5. Save the updated message
        Message updatedMessage = messageRepository.save(message);

        // 6. Map to DTO
        return MessageResponse.from(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId, UUID requesterId) {
        // 1. Find the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        // 2. Verify requester has permission to delete the message (sender or channel admin)
        channelAuthorizationService.verifyCanDeleteMessage(message.getChannel().getId(), message.getSender().getId(), requesterId);

        // 3. Delete the message
        messageRepository.delete(message);
    }
}
