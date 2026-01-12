package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.EditMessageRequest;
import com.trelix.trelix_app.dto.MessageResponse;
import com.trelix.trelix_app.dto.PagedMessageResponse;
import com.trelix.trelix_app.dto.SendMessageRequest;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, UUID senderId) {
        // 1. Verify sender has access to the channel by trying to get channel details
        // This will throw exception if user doesn't have access
        channelService.getChannelById(request.channelId(), senderId);

        // 2. Fetch Channel entity
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + request.channelId()));

        // 3. Fetch Sender User entity
        User sender = userService.findById(senderId);

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
        channelService.getChannelById(channelId, requesterId);

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
        if (message.getChannel() != null) {
            channelService.getChannelById(message.getChannel().getId(), requesterId);
        } else {
            throw new ResourceNotFoundException("Message is not associated with a channel");
        }

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
            throw new ForbiddenException("You are not authorized to edit this message.", ErrorCode.FORBIDDEN);
        }

        // 3. Check if within 15-minute editing window
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(message.getCreatedAt(), now);
        if (duration.toMinutes() > 15) {
            throw new ForbiddenException("Message can only be edited within 15 minutes of sending.",
                    ErrorCode.FORBIDDEN);
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

        // 2. Verify requester has permission to delete the message (sender or channel
        // admin)
        boolean isSender = message.getSender().getId().equals(requesterId);

        if (!isSender && message.getChannel() != null) {
            // Check if user is channel admin (would need channel admin check)
            // For now, only sender can delete
            throw new ForbiddenException("Only the message sender can delete this message.", ErrorCode.FORBIDDEN);
        }

        // 3. Delete the message
        messageRepository.delete(message);
    }
}
