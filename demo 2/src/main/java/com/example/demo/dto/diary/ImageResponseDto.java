package com.example.demo.dto.diary;

import com.example.demo.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {

    private Long id;
    private String originalName;
    private String imageUrl;

    public static ImageResponseDto toDto(Image image) {
        return new ImageResponseDto(
                image.getId(),
                image.getOriginalName(),
                getFullImageUrl(image.getImageUrl())
        );
    }

    private static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }
        return "" + imageUrl;
    }
}
