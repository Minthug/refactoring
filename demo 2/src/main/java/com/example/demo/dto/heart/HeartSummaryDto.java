package com.example.demo.dto.heart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeartSummaryDto {

    private Long totalHearts;
    private boolean isLikedByCurrentUser;
    private List<String> recentLikerUsernames;

}
