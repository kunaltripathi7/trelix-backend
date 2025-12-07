package com.trelix.trelix_app.service;

import com.cloudinary.Cloudinary;
import com.trelix.trelix_app.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload file to Cloudinary
     * @return public URL of uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map<String, Object> uploadParams = Map.of(
                    "folder", folder,
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new ServiceException("Failed to upload file to Cloudinary", e);
        }
    }

    /**
     * Delete file from Cloudinary
     * @param publicId extracted from URL
     */
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException e) {
            throw new ServiceException("Failed to delete file from Cloudinary", e);
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     */
    public String extractPublicId(String url) {
        // Example: https://res.cloudinary.com/<cloud>/image/upload/v123456/folder/filename.jpg
        // Return: folder/filename
        Pattern pattern = Pattern.compile(".*/upload/(?:v\\d+/)?([^\\.]+).*");
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid Cloudinary URL format: " + url);
    }
}
