package com.example.demo.repository;

import com.example.demo.entity.Follow;
import com.example.demo.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(Member follower, Member following);

    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);
    Page<Follow> findByFollowingId(Long followingId, Pageable pageable);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowingId(Long followingId);
    long countByFollowerId(Long followerId);
}
