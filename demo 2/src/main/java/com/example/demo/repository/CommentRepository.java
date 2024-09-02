package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByDiaryId(Long diaryId);

    List<Comment> findByDiaryId(Long diaryId, Pageable pageable);

    List<Comment> findByMemberId(Long memberId);

    List<Comment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("select count (C) from Comment c where c.diary.id = :diaryId")
    long countByDiaryId(@Param("diaryId") Long diaryId);

    @Query("select c from Comment c Join fetch c.diary d join fetch c.member m where c.id = :id")
    Optional<Comment> findByIdWithDiaryAndMember(@Param("id") Long id);
}
