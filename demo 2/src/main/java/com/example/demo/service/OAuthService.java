package com.example.demo.service;

import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.config.oauth.SocialOAuth;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final AuthService authService;
    private final SocialOAuth socialOAuth;

    public String request(String type) throws IOException {
        String redirectURL = socialOAuth.getOAuthRedirectURL(type);
        return redirectURL;
    }

}
