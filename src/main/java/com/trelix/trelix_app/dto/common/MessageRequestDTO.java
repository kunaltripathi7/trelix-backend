package com.trelix.trelix_app.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
    @NotBlank(message = "Message can't be blank")
    private String content;
}




