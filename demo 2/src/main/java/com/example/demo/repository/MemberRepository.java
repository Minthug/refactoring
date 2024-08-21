package com.example.demo.repository;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findByMemberTypeAndSocialId(MemberType memberType, String socialId);

    boolean existsByEmail(String email);
}
