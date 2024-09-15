package com.example.demo.controller;

import com.example.demo.config.utils.SortUtils;
import com.example.demo.dto.comment.CommentCreateRequestDto;
import com.example.demo.dto.comment.CommentDto;
import com.example.demo.dto.comment.CommentPageResponseDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.diary.DiaryCreatedRequestDto;
import com.example.demo.dto.diary.DiaryPatchDto;
import com.example.demo.dto.diary.DiaryResponseDto;
import com.example.demo.entity.Diary;
import com.example.demo.service.CommentService;
import com.example.demo.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final CommentService commentService;
    private final SortUtils sortUtils;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryResponseDto> createDiary(@Valid @RequestBody DiaryCreatedRequestDto request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Diary createDiary = diaryService.createDiary(request, userDetails.getUsername());
        DiaryResponseDto response = DiaryResponseDto.toDto(createDiary);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "")
    public ResponseEntity<?> findAllDiaries(@RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok(diaryService.findAllDiaries(page));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.findDiary(id));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> searchDiaries(@RequestParam String keyword,
                                           @RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok(diaryService.searchDiaries(keyword, page));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editDiary(@PathVariable Long id,
                                       @Valid @ModelAttribute DiaryPatchDto request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        DiaryResponseDto response = diaryService.updateDiary(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        diaryService.deleteDiary(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     *
     * @param diaryId
     * @param request
     * @param userDetails
     * @return
     *
     * CommentController를 쓰지않는 이유는 ?
     * 추후 확장성을 고려하면 CommentController를 만들어서 사용했겠지만,
     * 현재 CommentService의 기능이 단순하고, CommentController를 만들어서 사용할 필요가 없다고 판단했기 때문에
     * 1:1 설계 기획으로 진행했습니다.
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createDiaryComment(@PathVariable Long diaryId,
                                                         @Valid @RequestBody CommentCreateRequestDto request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        CommentDto response = commentService.createComment(diaryId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentPageResponseDto> getDiaryComments(@PathVariable Long diaryId,
                                                                         @RequestParam(defaultValue = "0") Integer page,
                                                                         @RequestParam(defaultValue = "20") Integer size,
                                                                         @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortUtils.createSortOrder(sort)));
        CommentPageResponseDto response = commentService.getCommentForDiary(diaryId, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<CommentDto> patchComments(@PathVariable Long diaryId,
                                                    @PathVariable Long commentId,
                                                    @Valid @RequestBody CommentUpdateDto request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        CommentDto response = commentService.updateComment(diaryId, commentId, new CommentUpdateDto(), userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long diaryId,
                                           @PathVariable long commentId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(diaryId, commentId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
