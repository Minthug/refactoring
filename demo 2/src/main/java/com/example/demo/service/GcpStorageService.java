package com.example.demo.service;

import com.example.demo.config.GcpStorageConfig;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
