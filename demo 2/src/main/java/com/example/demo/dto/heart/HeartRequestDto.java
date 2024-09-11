package com.example.demo.dto.heart;

import com.example.demo.entity.HeartType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeartRequestDto {

    @NotNull
    private Long memberId;

    @NotNull
    private Long targetId;

    @NotNull
    private HeartType heartType;

}
