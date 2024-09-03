package com.example.demo.dto.member;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String introduce;
    private String imageUrl;
    private MemberType memberType;

    public static MemberDto toDto(Member member) {
        if (member.getMemberType() == MemberType.GOOGLE) {
            member.updateProfileImageUrl(member.getImageUrl());
        } else if (member.getImageUrl() == null || member.getImageUrl().isEmpty()) {
            String defaultImageUrl = "";
            member.updateProfileImageUrl(defaultImageUrl);
        }
        return new MemberDto(member.getId(), member.getEmail(), member.getName(), member.getNickname(), member.getIntroduce(), member.getImageUrl(), member.getMemberType());
    }
}
