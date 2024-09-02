package com.example.demo.service;

import com.example.demo.config.GcpStorageConfig;
import com.example.demo.dto.ImageS3;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public ImageS3 uploadFile(Long id, MultipartFile profileImage) {
        return null;
    }
}
