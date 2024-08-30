package com.example.demo.config.oauth.handler;

import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.entity.JwtTokenType;
import com.example.demo.entity.MemberRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("Oauth Login Success");

        CustomOauthUser oauthUser = (CustomOauthUser) authentication.getPrincipal();

        if (oauthUser.getRole() == MemberRole.GUEST) {
            String accessToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.ACCESS_TOKEN);
            String refreshToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.REFRESH_TOKEN);
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            jwtService.sendAccessAndRefreshTokenCookie(response, accessToken, null);
            response.sendRedirect("http://localhost:3000");
        } else {
            loginSuccess(response, oauthUser);
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOauthUser oauthUser) throws IOException {
        String accessToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.createToken(oauthUser.getEmail(), JwtTokenType.REFRESH_TOKEN);
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshTokenCookie(response, accessToken, refreshToken);
        jwtService.updateAccessToken(oauthUser.getEmail(), accessToken);
        jwtService.updateRefreshToken(oauthUser.getEmail(), refreshToken);
        response.sendRedirect("http://localhost:3000");

    }
}
