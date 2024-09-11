package com.example.demo.dto.follow;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowRequestDto {

    @NotNull
    private Long followerId;

    @NotNull
    private Long followingId;

}
