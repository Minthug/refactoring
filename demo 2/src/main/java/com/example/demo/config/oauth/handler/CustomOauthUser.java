package com.example.demo.config.oauth.handler;

import com.example.demo.entity.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOauthUser extends DefaultOAuth2User {

    private String email;
    private MemberRole role;

    public CustomOauthUser(Collection<? extends GrantedAuthority> authorities,
                           Map<String, Object> attributes, String nameAttributeKey,
                           String email, MemberRole role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }
}
