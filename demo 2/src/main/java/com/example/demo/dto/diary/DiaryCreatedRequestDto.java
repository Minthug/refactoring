package com.example.demo.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiaryCreatedRequestDto {

    private String title;
    private String subTitle;
    private String content;
    private List<MultipartFile> imageFiles = new ArrayList<>();
}
