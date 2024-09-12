package com.example.demo.controller;

import com.example.demo.config.exception.DuplicateEmailException;
import com.example.demo.config.exception.DuplicateNicknameException;
import com.example.demo.config.exception.LoginFailureException;
import com.example.demo.config.exception.NotFoundException;
import com.example.demo.config.jwt.dto.TokenDto;
import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.dto.auth.LoginDto;
import com.example.demo.dto.auth.SignUpRequestDto;
import com.example.demo.dto.member.ProfileDto;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody SignUpRequestDto request) throws Exception {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "이메일을 입력해주세요."));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "비밀번호를 입력해주세요."));
        }

        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "닉네임을 입력해주세요."));
        }

        try {
            authService.signUp(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (DuplicateEmailException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (DuplicateNicknameException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "회원가입 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        try {
            return ResponseEntity.ok(authService.login(loginDto));
        } catch (LoginFailureException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ProfileDto profileDto = memberService.getProfile(email);
        return ResponseEntity.ok(profileDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenDto tokenDto, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(jwtService.reissue(tokenDto.getRefreshToken(), response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new TokenDto(null, null));
        }
    }
}
