package com.example.demo.service;

import com.example.demo.config.exception.EntityNotFoundException;
import com.example.demo.dto.comment.CommentCreateRequestDto;
import com.example.demo.dto.comment.CommentDto;
import com.example.demo.dto.comment.CommentPageResponseDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Member;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.DiaryRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentDto createComment(Long diaryId, CommentCreateRequestDto request, String username) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다."));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .diary(diary)
                .member(member)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentPageResponseDto getCommentForDiary(Long diaryId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByDiaryIdOrderByCreatedAtDesc(diaryId, pageable);

        List<CommentDto> commentDtos = commentPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return CommentPageResponseDto.builder()
                .commentList(commentDtos)
                .currentPage(commentPage.getNumber())
                .totalPage(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .build();
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentUpdateDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getMember().getId().equals(request.getMemberId())) {
            throw new IllegalArgumentException("해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.updateContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentPageResponseDto getMemberComments(Long memberId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        List<CommentDto> commentDtos = commentPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return CommentPageResponseDto.builder()
                .commentList(commentDtos)
                .currentPage(commentPage.getNumber())
                .totalPage(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .build();
    }


    private CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .diaryId(comment.getDiary().getId())
                .memberId(comment.getMember().getId())
                .memberName(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
