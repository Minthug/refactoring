package com.example.demo.dto.diary;

import com.example.demo.dto.member.MemberDto;
import com.example.demo.entity.Diary;
import com.example.demo.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Comparator;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponseDto {

    private Long id;
    private Long memberId;
    private String title;
    private String content;
    private String subTitle;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private String nickname;
    private String profileImageUrl;
//    private Integer likeCount;
//    private Integer commentCount;
//    private Boolean isLike;

    public static DiaryResponseDto toDto(Diary diary) {
        MemberDto memberDto = MemberDto.toDto(diary.getMember());

        String imageUrl = diary.getImages().stream()
                .max(Comparator.comparing(Image::getCreatedAt))
                .map(img -> ImageResponseDto.toDto(img).getImageUrl())
                .orElse(null);

        return new DiaryResponseDto(
                diary.getId(),
                memberDto.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getSubTitle(),
                diary.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant().toString(),
                diary.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant().toString(),
                memberDto.getImageUrl(),
                memberDto.getNickname(),
                imageUrl
        );
    }
}
