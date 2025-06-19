package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MessageRequestDTO;
import com.trelix.trelix_app.dto.MessageSummaryDTO;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.MessageComment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.MessageCommentRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.trelix.trelix_app.util.AppMapper.convertToMessageSummaryDTO;

@Service
@Transactional
@AllArgsConstructor
public class MessageCommentService {

    private final MessageCommentRepository messageCommentRepository;
    private final ChannelRepository channelRepository;
    private final AuthorizationService authService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    public MessageSummaryDTO addCommentToMessage(UUID channelId, UUID messageId, MessageRequestDTO commentRequest, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel related to the message can't be found."));
        UUID projectId = channel.getProject() != null ? channel.getProject().getId() : null;
        UUID teamId = channel.getTeam().getId();
        authService.checkMessageAccess(channelId, teamId ,projectId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User couldn't be found"));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        MessageComment messageComment = MessageComment.builder()
                .message(message)
                .user(user)
                .content(commentRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        return convertToMessageSummaryDTO(messageCommentRepository.save(messageComment));
    }

    public List<MessageSummaryDTO> getCommentsForMessage(UUID channelId, UUID messageId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with the id" + channelId));
        UUID projectId = channel.getProject() != null ? channel.getProject().getId() : null;
        UUID teamId = channel.getTeam().getId();
        authService.checkMessageAccess(channelId, teamId, projectId, userId);
        return messageCommentRepository.findByMessageIdOrderByCreatedAtAsc(messageId)
                .stream()
                .map(AppMapper::convertToMessageSummaryDTO)
                .toList();
    }

    public MessageSummaryDTO updateComment(UUID commentId,MessageRequestDTO commentRequest, UUID userId) {
        MessageComment messageComment = messageCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with the id" + commentId));
        authService.verifyMessageOwner(messageComment.getUser().getId(), userId);
        messageComment.setContent(commentRequest.getContent());
        return convertToMessageSummaryDTO(messageCommentRepository.save(messageComment));
    }

    public void deleteComment(UUID commentId, UUID id) {
        MessageComment messageComment = messageCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with the id" + commentId));
        authService.verifyMessageOwner(messageComment.getUser().getId(), id);
        messageCommentRepository.delete(messageComment);
    }
}
