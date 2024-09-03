package com.example.demo.entity;

import com.example.demo.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String introduce;

    @Column(name = "image_url")
    private String imageUrl;
    private String socialId;
    private String accessToken;
    private String refreshToken;
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;


    public void updateAccessToken(String updateAccessToken) {
        this.accessToken = updateAccessToken;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public void updateProfileImageUrl(String imageUrl) {
    }

    public void updateMember(String nickname, String introduce, String encryptedPassword, String imageUrl) {
        if (nickname != null && !nickname.isEmpty()) this.nickname = nickname;
        if (introduce != null) this.introduce = introduce;
        if (password != null && !password.isEmpty()) this.password = password;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
        onPreUpdate();
    }
}
