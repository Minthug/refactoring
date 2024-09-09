package com.example.demo.service;

import com.example.demo.config.exception.DuplicateEmailException;
import com.example.demo.config.exception.DuplicateNicknameException;
import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.dto.auth.SignUpRequestDto;
import com.example.demo.repository.MemberRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final MemberService memberService;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) throws Exception {

        validateUniqueInfo(signUpRequestDto);
        validateRequiredFields();
    }

    private void validateRequiredFields() {
    }

    private void validateUniqueInfo(SignUpRequestDto dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용중인 이메일입니다.");
        }

        if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.");
        }
    }

}
