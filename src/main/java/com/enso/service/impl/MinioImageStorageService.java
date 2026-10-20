package com.enso.service.impl;

import com.enso.service.ImageStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioImageStorageService implements ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        try {
            String originalName = file.getOriginalFilename();

            if (originalName == null || originalName.isBlank()) {
                originalName = "image";
            }
            originalName = originalName.replace('\\', '/');
            originalName = originalName.substring(originalName.lastIndexOf('/') + 1);
            String safeName = originalName.replaceAll("[^\\p{L}\\p{N}._-]", "_");

            if (safeName.isBlank()) {
                safeName = "image";
            }

            String objectName = UUID.randomUUID() + "-" + safeName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String encodedObjectName = UriUtils.encodePathSegment(objectName, StandardCharsets.UTF_8);

            return endpoint + "/" + bucketName + "/" + encodedObjectName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String normalizedUrl = imageUrl.trim().replace(" ", "%20");
            URI uri = URI.create(normalizedUrl);
            String path = uri.getPath();

            String bucketPrefix = "/" + bucketName + "/";
            int bucketIndex = path.indexOf(bucketPrefix);

            if (bucketIndex == -1) {
                throw new IllegalArgumentException("Image URL does not belong to the configured bucket");
            }

            String objectName = path.substring(bucketIndex + bucketPrefix.length());

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO", e);
        }

    }
}
