package com.example.demo.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowPageResponseDto {

    private List<FollowDto> follows;
    private int totalPage;
    private int currentPage;
    private long totalElements;
}
