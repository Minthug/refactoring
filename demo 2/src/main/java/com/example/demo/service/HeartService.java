package com.example.demo.service;

import com.example.demo.config.exception.EntityNotFoundException;
import com.example.demo.dto.heart.HeartDto;
import com.example.demo.dto.heart.HeartRequestDto;
import com.example.demo.dto.heart.HeartSummaryDto;
import com.example.demo.entity.Heart;
import com.example.demo.entity.HeartType;
import com.example.demo.entity.Member;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.HeartRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public HeartDto toggleHeart(HeartRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        validateTarget(request.getTargetId(), request.getHeartType());

        Optional<Heart> existingHeart = heartRepository.findByMemIdAndTarIdAndHeartType(request.getMemberId(), request.getTargetId(), request.getHeartType());

        if (existingHeart.isPresent()) {
            heartRepository.delete(existingHeart.get());
            return null; // 좋아요 취소
        } else {
            Heart heart = Heart.builder()
                    .member(member)
                    .targetId(request.getTargetId())
                    .heartType(request.getHeartType())
                    .build();
            Heart savedHeart = heartRepository.save(heart);
            return mapToDto(savedHeart);
        }
    }

    @Transactional
    public HeartSummaryDto heartSummary(Long targetId, HeartType heartType, Long currentMemberId) {
        long totalHearts = heartRepository.countByTargetIdAndHeartType(targetId, heartType);
        boolean isLikedByCurrentUser = heartRepository.existsByMemIdAndTarIdAndHeartType(currentMemberId, targetId, heartType);

        List<String> recentLikerNicknames = heartRepository.findTop3ByNicknames(targetId, heartType)
                .stream()
                .map(heart -> heart.getMember().getNickname())
                .collect(Collectors.toList());

        return HeartSummaryDto.builder()
                .totalHearts(totalHearts)
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .recentLikerNicknames(recentLikerNicknames)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<HeartDto> getHeartsByMem(Long memberId, Pageable pageable) {
        return heartRepository.findByMemberId(memberId, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<HeartDto> getHeartsByTarget(Long targetId, HeartType heartType, Pageable pageable) {
        return heartRepository.findByTargetIdAndHeartType(targetId, heartType, pageable)
                .map(this::mapToDto);
    }

    private void validateTarget(Long targetId, HeartType heartType) {
        if (heartType == HeartType.Diary) {
            diaryRepository.findById(targetId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다."));
        } else if (heartType == HeartType.Comment) {
            diaryRepository.findById(targetId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));
        }
    }

    private HeartDto mapToDto(Heart heart) {
        return HeartDto.builder()
                .id(heart.getId())
                .memberId(heart.getMember().getId())
                .memberNickname(heart.getMember().getNickname())
                .targetId(heart.getTargetId())
                .heartType(heart.getHeartType())
                .createdAt(heart.getCreatedAt())
                .build();
    }
}
