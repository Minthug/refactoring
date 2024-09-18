package com.example.demo.service;

import com.example.demo.config.GcpStorageConfig;
import com.example.demo.dto.ImageS3;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpStorageService {

    private final GcpStorageConfig gcpStorageConfig;
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private final String bucketName;

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

    public ImageS3 uploadProfile(Long memberId, MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        String uploadFilePath = String.format("member/%d/profile", memberId);
        List<MultipartFile> fileList = Collections.singletonList(imageFile);

        List<ImageS3> uploadResult = uploadFile(uploadFilePath, fileList);

        if (uploadResult.isEmpty()) {
            throw new FileUploadException("Failed to upload file");
        }

        ImageS3 result = uploadResult.get(0);
        if (result.getUploadFileUrl() == null || result.getUploadFileUrl().isEmpty()) {
            throw new FileUploadException("Failed to upload file");
        }

        return result;
    }

    private ImageS3 uploadSingleFile(String uploadFilePath, MultipartFile imageFile) {
        return null;
    }

    private ImageS3 crateImageS3(String originalFilename, String message) {
        return null;
    }
}
