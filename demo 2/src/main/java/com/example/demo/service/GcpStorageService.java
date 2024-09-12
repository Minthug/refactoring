package com.example.demo.service;

import com.example.demo.config.GcpStorageConfig;
import com.example.demo.dto.ImageS3;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpStorageService {

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public GcpStorageService(Storage storage, GcpStorageConfig gcpStorageConfig) {
        this.storage = storage;
        this.bucketName = gcpStorageConfig.getBucketName();
    }

    public boolean deleteFile(String imageUrl) {
        return false;
    }

    public List<ImageS3> uploadDiaryFile(Long memberId, Long boardId, List<MultipartFile> imageFiles) {
        String uploadFilePath = String.format("member/%d/diary/%d", memberId, boardId);
        return uploadFile(uploadFilePath, imageFiles);
    }

    private List<ImageS3> uploadFile(String uploadFilePath, List<MultipartFile> imageFiles) {
        List<ImageS3> s3Files = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile.isEmpty()) {
                continue;
            }
            try {
                ImageS3 uploadResult = uploadSingleFile(uploadFilePath, imageFile);
                s3Files.add(uploadResult);
            } catch (Exception e) {
                log.error("Failed to upload file: {}", imageFile.getOriginalFilename(), e);
                s3Files.add(crateImageS3(imageFile.getOriginalFilename(), e.getMessage()));
            }
        }
        return s3Files;
    }

    private ImageS3 uploadSingleFile(String uploadFilePath, MultipartFile imageFile) {
        return null;
    }

    private ImageS3 crateImageS3(String originalFilename, String message) {
        return null;
    }
}
