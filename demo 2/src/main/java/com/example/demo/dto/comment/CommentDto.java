package com.example.demo.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private Long memberId;
    private Long diaryId;
    private String content;
    private String memberName;
    private String userImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
