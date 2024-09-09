package com.example.demo.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentPageResponseDto {

    private List<CommentDto> commentList;
    private int totalPage;
    private int currentPage;
    private long totalElements;
}
