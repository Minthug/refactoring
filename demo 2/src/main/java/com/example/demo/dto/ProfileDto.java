package com.example.demo.dto;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long id;
    private String nickname;
    private String email;
    private String imageUrl;

    public static ProfileDto fromMember(Member member) {
        String imageUrl = member.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "";
        }

        return new ProfileDto(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                imageUrl
        );
    }
}
