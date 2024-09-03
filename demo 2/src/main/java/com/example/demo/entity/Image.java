package com.example.demo.entity;

import com.example.demo.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;

    private String uniqueName;
    private String imageUrl;

    private String urlPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Image(String originalName, String imageUrl, String urlPath) {
        this.originalName = originalName;
        this.imageUrl = imageUrl;
        this.urlPath = urlPath;
        this.uniqueName = generateUniqueName(extractExtension(originalName);
    }

    private final static String[] supportedExtensions = new String[]{"jpg", "jpeg", "png", "gif"};


    private String extractExtension(String originalName) {
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == originalName.length() - 1) {
            throw new UnsupportedOperationException("파일 확장자를 찾을 수 없습니다.");
        }
        String extension = originalName.substring(dotIndex + 1).toLowerCase();
        if (!isSupportedFormat(extension)) {
            throw new UnsupportedOperationException("지원하지 않는 파일 형식입니다.");
        }
        return extension;
    }


    private boolean isSupportedFormat(String extension) {
        return Arrays.asList(supportedExtensions).contains(extension);
    }


    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }
}
