package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.MessageComment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.MessageCommentRepository;
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
@Transactional
@RequiredArgsConstructor
public class MessageCommentService {

    private final MessageCommentRepository messageCommentRepository;
    private final AuthorizationService authService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageSummaryDTO addCommentToMessage(UUID messageId, MessageRequestDTO commentRequest, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        authService.checkChannelAccess(message.getChannel(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        MessageComment messageComment = MessageComment.builder()
                .message(message)
                .user(user)
                .content(commentRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        return AppMapper.convertToMessageSummaryDTO(messageCommentRepository.save(messageComment));
    }

    public List<MessageSummaryDTO> getCommentsForMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        authService.checkChannelAccess(message.getChannel(), userId);
        return messageCommentRepository.findByMessageIdOrderByCreatedAtAsc(messageId)
                .stream()
                .map(AppMapper::convertToMessageSummaryDTO)
                .toList();
    }

    public MessageSummaryDTO updateComment(UUID commentId, MessageRequestDTO commentRequest, UUID userId) {
        MessageComment messageComment = messageCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        authService.verifyMessageOwner(messageComment.getUser().getId(), userId);
        messageComment.setContent(commentRequest.getContent());
        messageComment.setUpdatedAt(LocalDateTime.now());
        return AppMapper.convertToMessageSummaryDTO(messageCommentRepository.save(messageComment));
    }

    public void deleteComment(UUID commentId, UUID userId) {
        MessageComment messageComment = messageCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        authService.deleteMessageAccess(messageComment, userId);
        messageCommentRepository.delete(messageComment);
    }
}
