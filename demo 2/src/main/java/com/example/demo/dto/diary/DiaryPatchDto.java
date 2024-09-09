package com.example.demo.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DiaryPatchDto {

    private String title;
    private String subTitle;
    private String content;
    private List<MultipartFile> imageFiles = new ArrayList<>();
    private List<Long> deletedImages = new ArrayList<>();

}
