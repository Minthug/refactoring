package com.example.demo.config.jwt.handler;

import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.entity.JwtTokenType;
import com.example.demo.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = extractUsername(authentication);
        String accessToken = jwtService.createToken(email, JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.createToken(email, JwtTokenType.REFRESH_TOKEN);

        jwtService.sendAccessAndRefreshTokenCookie(response, accessToken, refreshToken);

        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    member.updateRefreshToken(refreshToken);
                    memberRepository.saveAndFlush(member);
                });
        log.info("Login Success: {}", email);
        log.info("Login Success, Access Token: {}", accessToken);
        log.info("발급된 AccessToken 만료기간 : {}", accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}
