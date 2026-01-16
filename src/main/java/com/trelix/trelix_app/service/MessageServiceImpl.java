package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.EditMessageRequest;
import com.trelix.trelix_app.dto.response.MessageResponse;
import com.trelix.trelix_app.dto.response.PagedMessageResponse;
import com.trelix.trelix_app.dto.request.SendMessageRequest;
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
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, UUID senderId) {

        channelService.getChannelById(request.channelId(), senderId);

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + request.channelId()));

        User sender = userService.findById(senderId);

        Message message = Message.builder()
                .channel(channel)
                .sender(sender)
                .content(request.content())
                .build();

        Message savedMessage = messageRepository.save(message);

        MessageResponse response = MessageResponse.from(savedMessage);

        webSocketService.broadcastToChannel(request.channelId(), response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedMessageResponse getMessages(UUID channelId, int page, int size, UUID requesterId) {
        channelService.getChannelById(channelId, requesterId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<Message> messagePage = messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId, pageable);

        return PagedMessageResponse.from(messagePage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(UUID messageId, UUID requesterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (message.getChannel() != null) {
            channelService.getChannelById(message.getChannel().getId(), requesterId);
        } else {
            throw new ResourceNotFoundException("Message is not associated with a channel");
        }

        return MessageResponse.from(message);
    }

    @Override
    @Transactional
    public MessageResponse editMessage(UUID messageId, EditMessageRequest request, UUID requesterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (!message.getSender().getId().equals(requesterId)) {
            throw new ForbiddenException("You are not authorized to edit this message.", ErrorCode.FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(message.getCreatedAt(), now);
        if (duration.toMinutes() > 15) {
            throw new ForbiddenException("Message can only be edited within 15 minutes of sending.",
                    ErrorCode.FORBIDDEN);
        }

        message.setContent(request.content());
        message.setEditedAt(now);

        Message updatedMessage = messageRepository.save(message);

        return MessageResponse.from(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId, UUID requesterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        boolean isSender = message.getSender().getId().equals(requesterId);

        if (!isSender && message.getChannel() != null) {
            throw new ForbiddenException("Only the message sender can delete this message.", ErrorCode.FORBIDDEN);
        }

        messageRepository.delete(message);
    }
}




