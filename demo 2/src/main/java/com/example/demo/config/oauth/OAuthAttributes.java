package com.example.demo.config.oauth;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberRole;
import com.example.demo.entity.MemberType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private String nameAttributeKey;
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(MemberType memberType, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new GoogleOAuthUserInfo(attributes))
                .build();
    }

    public Member toEntity(MemberType memberType, OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .memberType(memberType)
                .socialId(oAuth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(oAuth2UserInfo.getNickname())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .memberRole(MemberRole.GUEST)
                .build();
    }
}
