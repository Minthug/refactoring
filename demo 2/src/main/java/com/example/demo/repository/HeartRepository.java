package com.example.demo.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.demo.entity.Heart;
import com.example.demo.entity.HeartType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    Optional<Heart> findByMemIdAndTarIdAndHeartType(Long memberId, Long targetId, HeartType heartType);

    long countByTargetIdAndHeartType(Long targetId, HeartType heartType);

    boolean existsByMemIdAndTarIdAndHeartType(Long currentMemberId, Long targetId, HeartType heartType);

    List<Heart> findTop3ByNicknames(Long targetId, HeartType heartType);

    Page<Heart> findByMemberId(Long memberId, Pageable pageable);

    Page<Heart> findByTargetIdAndHeartType(Long targetId, HeartType heartType, Pageable pageable);
}
