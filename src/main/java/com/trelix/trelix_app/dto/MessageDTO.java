package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MessageDTO {
    private UUID id;
    private String content;
    private UUID senderId;
    private LocalDateTime sentAt;
    private String senderUsername;
    List<MessageCommentDTO> comments;
    List<AttachmentDTO> attachments;
}
