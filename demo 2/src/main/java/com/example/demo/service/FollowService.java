package com.example.demo.service;

import com.example.demo.config.exception.EntityNotFoundException;
import com.example.demo.dto.follow.FollowDto;
import com.example.demo.dto.follow.FollowPageResponseDto;
import com.example.demo.entity.Follow;
import com.example.demo.entity.Member;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FollowDto follow(Long followeeId, String followerUsername) {
        Member follower = memberRepository.findByName(followerUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        Member following = memberRepository.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(following)
                .build();

        Follow savedFollow = followRepository.save(follow);
        return mapToDto(savedFollow);
    }

    @Transactional
    public void unfollow(Long followeeId, String followerUsername) {

        Member follower = memberRepository.findByName(followerUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        Member following = memberRepository.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 중이 아닙니다."));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public FollowPageResponseDto getFollowers(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        Page<Follow> followPage = followRepository.findByFollowerId(memberId, pageable);
        return createFollowPage(followPage);
    }

    @Transactional(readOnly = true)
    public FollowPageResponseDto getFollowing(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        Page<Follow> followPage = followRepository.findByFollowingId(memberId, pageable);
        return createFollowPage(followPage);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Transactional
    public long isFollowerCount(Long memberId) {
        return followRepository.countByFollowingId(memberId);
    }

    @Transactional
    public long getFollowingCount(Long memberId) {
        return followRepository.countByFollowerId(memberId);
    }


    private FollowPageResponseDto createFollowPage(Page<Follow> followPage) {
        List<FollowDto> followDtos = followPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return FollowPageResponseDto.builder()
                .follows(followDtos)
                .currentPage(followPage.getNumber())
                .totalPage(followPage.getTotalPages())
                .totalElements(followPage.getTotalElements())
                .build();
    }

    private FollowDto mapToDto(Follow follow) {
        return FollowDto.builder()
                .id(follow.getId())
                .followerId(follow.getFollower().getId())
                .followingId(follow.getFollowee().getId())
                .followerUsername(follow.getFollower().getName())
                .followingUsername(follow.getFollowee().getName())
                .createdAt(follow.getCreatedAt())
                .build();
    }
}
