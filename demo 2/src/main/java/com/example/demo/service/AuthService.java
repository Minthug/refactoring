package com.example.demo.service;

import com.example.demo.config.exception.DuplicateEmailException;
import com.example.demo.config.exception.DuplicateNicknameException;
import com.example.demo.config.jwt.dto.TokenDto;
import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.dto.auth.LoginDto;
import com.example.demo.dto.auth.SignUpRequestDto;
import com.example.demo.entity.JwtTokenType;
import com.example.demo.entity.Member;
import com.example.demo.entity.MemberRole;
import com.example.demo.entity.MemberType;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;

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
        validateRequiredFields(signUpRequestDto);

        Member member = Member.builder()
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .nickname(signUpRequestDto.getNickname())
                .introduce(signUpRequestDto.getIntroduce())
                .imageUrl(signUpRequestDto.getImageUrl())
                .memberRole(MemberRole.USER)
                .memberType(MemberType.LOCAL)
                .build();

        member.hashPassword(encoder);
        memberRepository.save(member);
    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        if (member.getMemberType() == null) {
            member.updateMemberType(MemberType.LOCAL);
            memberRepository.save(member);
        }

        if (member.getMemberType() == MemberType.LOCAL) {
            if (!encoder.matches(loginDto.getPassword(), member.getPassword())) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }
        }

        String accessToken = jwtService.createToken(member.getEmail(), JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.createToken(member.getEmail(), JwtTokenType.REFRESH_TOKEN);

        jwtService.updateAccessToken(member.getEmail(), refreshToken);
        jwtService.updateRefreshToken(accessToken, refreshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public Member registerOrUpdateMember(String email, String name, String providerId, String provider, String imageUrl) {
        MemberType memberType;
        if (provider.equals("google")) {
            memberType = MemberType.GOOGLE;
        } else {
            memberType = MemberType.LOCAL;
        }

        return memberRepository.findByEmail(email)
                .map(member -> {
                    member.updateName(name);
                    member.updateOAuthInfo(providerId, provider);
                    member.updateMemberType(memberType);
                    member.updateProfileImageUrl(imageUrl);
                    return member;
                })
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .name(name)
                            .socialId(providerId)
                            .provider(provider)
                            .imageUrl(imageUrl)
                            .memberType(memberType)
                            .memberRole(MemberRole.USER)
                            .build();
                    return memberRepository.save(newMember);
                });
    }

    private void validateRequiredFields(SignUpRequestDto dto) {
        if (StringUtils.isEmpty(dto.getEmail())){
            throw new InvalidParameterException("이메일은 필수 입력값입니다.");
        }

        if (StringUtils.isEmpty(dto.getPassword())){
            throw new InvalidParameterException("비밀번호는 필수 입력값입니다.");
        }

        if (StringUtils.isEmpty(dto.getNickname())){
            throw new InvalidParameterException("닉네임은 필수 입력값입니다.");
        }
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
