package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MessageDetailDTO;
import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final AuthorizationService authService;
    private final UserRepository userRepository;

    public MessageDetailDTO createMessage(UUID channelId, MessageRequestDTO messageRequest, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        authService.checkChannelAccess(channel, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Message message = Message.builder()
                .channel(channel)
                .sender(user)
                .content(messageRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        return AppMapper.convertToMessageDetailDTO(messageRepository.save(message));
    }

    public List<MessageSummaryDTO> getMessages(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        authService.checkChannelAccess(channel, userId);
        List<Message> messages = messageRepository.findByChannelId(channelId);
        return messages.stream().map(AppMapper::convertToMessageSummaryDTO).toList();
    }

    public MessageDetailDTO getMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        authService.checkChannelAccess(message.getChannel(), userId);
        return AppMapper.convertToMessageDetailDTO(message);
    }

    public MessageDetailDTO updateMessage(UUID messageId, MessageRequestDTO messageRequest, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        authService.verifyMessageOwner(message.getSender().getId(), userId);
        message.setContent(messageRequest.getContent());
        message.setUpdatedAt(LocalDateTime.now());
        return AppMapper.convertToMessageDetailDTO(messageRepository.save(message));
    }

    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        authService.verifyMessageOwner(message, userId);
        messageRepository.delete(message);
    }
}
