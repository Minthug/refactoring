package com.example.demo.repository;

import com.example.demo.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Diary findByTitle(String title);

    @Query(value = "select d from Diary d join fetch d.member m",
    countQuery = "select count(d) from Diary d")
    Page<Diary> findAll(Pageable pageable);

    @Query("select d from Diary d JOIN FETCH d.member m JOIN FETCH d.images where d.id = :id")
    Optional<Diary> findByIdWithMemberAndImages(@Param("id") Long id);

    @Query("select d from Diary d JOIN FETCH d.member m where d.id IN :ids")
    Optional<Diary> findDiariesByWithMemberByIds(@Param("id") List<Long> ids);
}
