package com.example.demo.controller;

import com.example.demo.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth")
public class OAuthController {

    private final OAuthService oAuthService;


}
