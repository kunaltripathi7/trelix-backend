package com.trelix.trelix_app.service;

import com.cloudinary.Cloudinary;
import com.trelix.trelix_app.dto.AttachmentDTO;
import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.AttachmentRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final Cloudinary cloudinary;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void uploadAttachment(MultipartFile file, UUID taskId, UUID userId, UUID messageId) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found for task ID: " + taskId));


        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .url(uploadResult.get("url").toString())
                .uploadedBy(user)
                .task(task)
                .createdAt(java.time.LocalDateTime.now())
                .message(message)
                .build();

        attachmentRepository.save(attachment);

    }

    public List<AttachmentDTO> getAttachments(UUID taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(AppMapper::convertToAttachmentDTO)
                .toList();
    }

    public void deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));
        attachmentRepository.delete(attachment);
    }

}
