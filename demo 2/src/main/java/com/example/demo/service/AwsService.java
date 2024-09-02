package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.demo.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsService {

    private final AmazonS3Client amazonS3Client;
    private final S3Config s3Config;

}
