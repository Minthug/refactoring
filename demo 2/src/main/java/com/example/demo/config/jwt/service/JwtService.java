package com.example.demo.config.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.jwt.dto.TokenDto;
import com.example.demo.entity.JwtTokenType;
import com.example.demo.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.Optional;

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

    private final MemberRepository memberRepository;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    public String createToken(String email, JwtTokenType tokenType){
        Date now = new Date();
        Date expiration;
        String subject;

        if (tokenType == JwtTokenType.ACCESS_TOKEN){
            expiration = new Date(now.getTime() + accessTokenExpirationPeriod);
            subject = ACCESS_TOKEN_SUBJECT;
        } else {
            expiration = new Date(now.getTime() + refreshTokenExpirationPeriod);
            subject = REFRESH_TOKEN_SUBJECT;
        }

        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiration)
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    private String extractEmailFromToken(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
        return jwt.getClaim(EMAIL_CLAIM).asString();
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 access token: {}", accessToken);
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access token, Refresh token 헤더 설정 완료.");
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public void sendAccessTokenCookie(HttpServletResponse response, String accessToken) {
        CookieUtil.addCookie(response, "accessToken", accessToken, accessTokenExpirationPeriod / 1000, true);
    }

    public void sendRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        CookieUtil.addCookie(response, "refreshToken", refreshToken, refreshTokenExpirationPeriod / 1000, true);
    }

    public void sendAccessAndRefreshTokenCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        sendAccessTokenCookie(response, accessToken);
        sendRefreshTokenCookie(response, refreshToken);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        log.info("Attempting to extract Refresh token");

        Optional<String> fromHeader = Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));

        if (fromHeader.isPresent()) {
            log.info("Refresh token extracted from header");
            return fromHeader;
        }

        fromHeader = Optional.ofNullable(request.getHeader(refreshHeader));
        if (fromHeader.isPresent()) {
            log.info("Refresh token extracted from header without Bearer prefix");
            return fromHeader;
        }

        Optional<String> fromCookie = extractTokenFromCookie(request, "refreshToken");
        if (fromCookie.isPresent()) {
            log.info("Refresh token extracted from cookie");
            return fromCookie;
        }

        String fromParameter = request.getParameter("refreshToken");
        if (fromParameter != null && !fromParameter.isEmpty()) {
            log.info("Refresh token extracted from parameter");
            return Optional.of(fromParameter);
        }

        log.warn("Refresh token not found");
        return Optional.empty();
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public void updateRefreshToken(String email, String refreshToken) {
        memberRepository.findByEmail(email)
                .ifPresentOrElse(
                        member -> member.updateRefreshToken(refreshToken),
                        () -> log.error("해당하는 회원이 없습니다.")
                );
    }

    public void updateAccessToken(String email, String accessToken) {
        memberRepository.findByEmail(email)
                .ifPresentOrElse(
                        member -> member.updateAccessToken(accessToken),
                        () -> log.error("해당하는 회원이 없습니다.")
                );
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public TokenDto reissue(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidParameterException("Refresh Token이 없습니다.");
        }
        // Refresh Token 유효성 검사
        if (!isTokenValid(refreshToken)) {
            throw new InvalidParameterException("유효하지 않은 Refresh Token입니다.");
        }

        try {
            // Refresh Token에서 이메일 추출
            String email = extractEmailFromToken(refreshToken);

            // DB에서 저장된 RefreshToken 확인 (필요한 경우)

            // 새로운 Access Token 생성
            String newAccessToken = createToken(email, JwtTokenType.ACCESS_TOKEN);

            // 새로운 Refresh Token 생성 (선택적)
            String newRefreshToken = createToken(email, JwtTokenType.REFRESH_TOKEN);

            // Access Token을 응답 헤더에 추가
            sendAccessToken(response, newAccessToken);

            return new TokenDto(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new InvalidParameterException("토큰 재발급 중 오류가 발생했습니다.");
        }
    }

    private static class CookieUtil {
        public static void addCookie(HttpServletResponse response, String name, String value, long maxAge, boolean httpOnly) {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 Days
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }


    }
}

