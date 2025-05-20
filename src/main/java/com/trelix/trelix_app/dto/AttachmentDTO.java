package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private UUID uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}