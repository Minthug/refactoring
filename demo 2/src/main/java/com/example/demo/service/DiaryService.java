package com.example.demo.service;

import com.example.demo.config.exception.BusinessLogicException;
import com.example.demo.dto.ImageS3;
import com.example.demo.dto.diary.*;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Image;
import com.example.demo.entity.Member;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GcpStorageService gcpStorageService;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public Diary createDiary(DiaryCreatedRequestDto request, String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Diary diary = new Diary(
                request.getTitle(),
                request.getSubTitle(),
                request.getContent(),
                member,
                request.getImageFiles());

                diary = diaryRepository.save(diary);

                if (!request.getImageFiles().isEmpty()) {
                    List<ImageS3> uploadFiles = gcpStorageService.uploadDiaryFile(member.getId(), diary.getId(), request.getImageFiles());

                    List<Image> images = uploadFiles.stream()
                            .map(file -> new Image(file.getOriginalFileName(), file.getUploadFileUrl(), file.getUploadFilePath()))
                            .collect(Collectors.toList());

                    images.forEach(diary::addImages);

                    diaryRepository.save(diary);
                }
                return diary;
    }

    @Transactional(readOnly = true)
    public DiaryResponseDto findDiary(Long id) {
        return diaryRepository.findByIdWithMemberAndImages(id)
                .map(DiaryResponseDto::toDto)
                .orElseThrow(() -> new BusinessLogicException("해당 일기가 존재하지 않습니다."));
    }

    @Transactional
    public DiaryFindAllWithPagingDto findAllDiaries(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Diary> diaries = diaryRepository.findAll(pageRequest);
        List<DiaryFindAllResponseDto> diariesWithDto = diaries.stream()
                .map(DiaryFindAllResponseDto::toDto)
                .collect(Collectors.toList());
        return DiaryFindAllWithPagingDto.toDto(diariesWithDto, new PageInfoDto(diaries));
    }

    @Transactional(readOnly = true)
    public DiaryFindAllWithPagingDto searchDiaries(String keyword, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Diary> diaries = diaryRepository.findAllByTitleContaining(keyword, pageRequest);

        List<Long> diaryIds = diaries.stream()
                .map(Diary::getId)
                .collect(Collectors.toList());

        List<Diary> diariesWithMember = diaryRepository.findDiariesByWithMemberByIds(diaryIds);

        List<DiaryFindAllResponseDto> diariesWithDto = diariesWithMember.stream()
                .map(DiaryFindAllResponseDto::toDto)
                .collect(Collectors.toList());

        return DiaryFindAllWithPagingDto.toDto(diariesWithDto, new PageInfoDto(diaries), keyword);
    }

    @Transactional
    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException("해당 일기가 존재하지 않습니다."));

        diaryRepository.delete(diary);
    }

    @Transactional
    public DiaryResponseDto updateDiary(Long id, DiaryPatchDto request) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException("해당 일기가 존재하지 않습니다."));

        diary.update(request.getTitle(), request.getSubTitle(), request.getContent());

        return DiaryResponseDto.toDto(diary);
    }
}
