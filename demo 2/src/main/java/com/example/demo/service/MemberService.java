package com.example.demo.service;

import com.example.demo.dto.MemberDto;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final DiaryRepository diaryRepository;
    private final GcpStorageService gcpStorageService;

    @Transactional
    public List<MemberDto> findAllMember() {
        return memberRepository.findAll().stream()
                .map(MemberDto::toDto)
                .collect(Collectors.toList());
    }



}
