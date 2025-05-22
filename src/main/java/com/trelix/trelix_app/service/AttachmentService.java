package com.trelix.trelix_app.service;

import com.cloudinary.Cloudinary;
import com.trelix.trelix_app.repository.AttachmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final Cloudinary cloudinary;

    public String uploadAttachment(MultipartFile file) {


    }

}
