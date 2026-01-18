package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.trelix.trelix_app.validation.EitherTaskOrMessage;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EitherTaskOrMessage
public class CreateCommentRequest {
    private UUID taskId;
    private UUID messageId;

    @NotBlank(message = "Content cannot be empty")
    private String content;
}
