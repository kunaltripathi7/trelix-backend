package com.trelix.trelix_app.dto.response;

import java.util.UUID;

public record AttachmentDownloadResponse(
        String downloadUrl,
        String fileName,
        Long fileSize
) {}




