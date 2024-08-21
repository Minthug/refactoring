package com.example.demo.config.jwt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private String secretKey;

    private Long accessTokenExpirationPeriod;

    private Long refreshTokenExpirationPeriod;

    private String accessHeader;

    private String refreshHeader;


}
