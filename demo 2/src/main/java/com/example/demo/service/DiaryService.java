package com.example.demo.service;

import com.example.demo.dto.ImageS3;
import com.example.demo.dto.diary.DiaryCreatedRequestDto;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Image;
import com.example.demo.entity.Member;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


}
