package com.trelix.trelix_app.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MessageSummaryDTO {
    private UUID id;
    private String content;
    private UUID senderId;
    private LocalDateTime sentAt;
    private String senderUsername;
}




