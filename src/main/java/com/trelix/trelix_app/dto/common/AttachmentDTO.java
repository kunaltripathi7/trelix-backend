package com.trelix.trelix_app.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String fileType;
    private long fileSize;
    private LocalDateTime createdAt;
}




