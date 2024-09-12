package com.example.demo.controller;

import com.example.demo.dto.diary.DiaryCreatedRequestDto;
import com.example.demo.dto.diary.DiaryPatchDto;
import com.example.demo.dto.diary.DiaryResponseDto;
import com.example.demo.entity.Diary;
import com.example.demo.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryResponseDto> createDiary(@Valid @ModelAttribute DiaryCreatedRequestDto request,
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
}
