package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Image;
import com.example.demo.entity.Member;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional(readOnly = true)
    public List<MemberDto> findAllMember() {
        return memberRepository.findAll().stream()
                .map(MemberDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberDto findMember(Long id) {
        return memberRepository.findById(id)
                .map(MemberDto::toDto)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = findMemberById(id);
        memberRepository.deleteById(id);
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public ProfileDto getProfile(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        return ProfileDto.fromMember(member);
    }

    @Transactional
    public MemberResponseDto editMember(Long id, MemberUpdateDto memberUpdateDto) {
        log.info("Editing Member with id: {}", id);
        Member member = findMemberById(id);

        try {
            validateMemberPermission(member);

            String encryptedPassword = encryptPasswordIfProvided(memberUpdateDto.getPassword());
            ImageS3 updateImage = updateProfileImage(member, memberUpdateDto.getImagesFiles());

        }
    }

    private ImageS3 updateProfileImage(Member member, List<MultipartFile> imagesFiles) {
        if (imagesFiles == null || imagesFiles.isEmpty() || imagesFiles.get(0).isEmpty()) {
            log.info("No image file provided");
            return null;
        }
        try {
            String existingImageUrl = member.getImageUrl(); // 기존 이미지 URL
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                boolean deleted = deleteExistingImage(existingImageUrl);
                if (!deleted) {
                    throw new IllegalArgumentException("기존 이미지 삭제에 실패했습니다.");
                }
            }

            MultipartFile profileImage = imagesFiles.get(0);
            ImageS3 uploadImage = gcpStorageService.uploadFile(member.getId(), profileImage);
            return uploadImage;
        } catch (Exception e) {
            log.error("Failed to update profile image: {}", e.getMessage());
            throw new RuntimeException("프로필 이미지 업데이트에 실패했습니다.");
        }
    }

    private boolean deleteExistingImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return true;
        }

        try {
            boolean deleted = gcpStorageService.deleteFile(imageUrl);
            if (deleted) {
                log.error("Failed to delete existing image: {}", imageUrl);
                return true;
            } else {
                log.info("Existing image deleted: {}", imageUrl);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to delete existing image: {}", imageUrl);
            return false;
        }
    }

    private String encryptPasswordIfProvided(String password) {
        if (password != null && !password.isEmpty()) {
            String encryptedPassword = encoder.encode(password);
            log.info("Password encrypted: {}", encryptedPassword);
            return encryptedPassword;
        }
        return null;
    }

    private void validateMemberPermission(Member member) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails) || !((UserDetails) authentication.getPrincipal()).getUsername().equals(member.getEmail())) {
            throw new IllegalArgumentException("해당 회원의 정보를 수정할 권한이 없습니다.");
        }
    }


}
