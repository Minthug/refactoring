package com.example.demo.dto.heart;

import com.example.demo.entity.HeartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeartDto {

    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long targetId;
    private HeartType heartType;
    private LocalDateTime createdAt;

}
