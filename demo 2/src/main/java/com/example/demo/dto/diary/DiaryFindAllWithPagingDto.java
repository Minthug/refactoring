package com.example.demo.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryFindAllWithPagingDto {

    private List<DiaryFindAllResponseDto> data;
    private PageInfoDto pageInfo;
    private String keyword;

    public DiaryFindAllWithPagingDto(List<DiaryFindAllResponseDto> data, PageInfoDto pageInfo) {
        this(data, pageInfo, null);
    }

    public static DiaryFindAllWithPagingDto toDto(List<DiaryFindAllResponseDto> data, PageInfoDto pageInfo) {
        return new DiaryFindAllWithPagingDto(data, pageInfo);
    }

    public static DiaryFindAllWithPagingDto toDto(List<DiaryFindAllResponseDto> data, PageInfoDto pageInfo, String keyword) {
        return new DiaryFindAllWithPagingDto(data, pageInfo, keyword);
    }
}
