package com.example.demo.dto.diary;

import com.example.demo.dto.member.MemberDto;
import com.example.demo.entity.Diary;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZoneId;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DiaryFindAllResponseDto {
    private Long id;
    private String title;
    private String subTitle;
    private String content;
    private String imageUrl;
    private String nickname;
    private String profileUrl;
    private String createdAt;
    private String updatedAt;


    public static DiaryFindAllResponseDto toDto(Diary diary) {
        MemberDto memberDto = MemberDto.toDto(diary.getMember());

        String image = diary.getImages().stream()
                .map(img -> ImageResponseDto.toDto(img).getImageUrl())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return new DiaryFindAllResponseDto(
                diary.getId(),
                diary.getTitle(),
                diary.getSubTitle(),
                diary.getContent(),
                image,
                memberDto.getNickname(),
                memberDto.getImageUrl(),
                diary.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant().toString(),
                diary.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant().toString()
        );
    }
}
