package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.EditMessageRequest;
import com.trelix.trelix_app.dto.MessageResponse;
import com.trelix.trelix_app.dto.PagedMessageResponse;
import com.trelix.trelix_app.dto.SendMessageRequest;

import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(SendMessageRequest request, UUID senderId);

    PagedMessageResponse getMessages(UUID channelId, int page, int size, UUID requesterId);

    MessageResponse getMessageById(UUID messageId, UUID requesterId);

    MessageResponse editMessage(UUID messageId, EditMessageRequest request, UUID requesterId);

    void deleteMessage(UUID messageId, UUID requesterId);
}
