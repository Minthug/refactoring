package com.example.demo.dto.member;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String introduce;
    private String imageUrl;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getIntroduce(),
                member.getImageUrl());
    }
}
