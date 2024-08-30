package com.example.demo.config.oauth;

import com.example.demo.config.oauth.handler.CustomOauthUser;
import com.example.demo.entity.Member;
import com.example.demo.entity.MemberType;
import com.example.demo.entity.OauthUser;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.OauthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final OauthUserRepository oauthUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOauthUserService.loadUser() - Oauth2 login request");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        MemberType memberType = getMemberType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(memberType, userNameAttributeName, attributes);

        Member createMember = getMember(extractAttributes, memberType);

        return new CustomOauthUser(
                Collections.singleton(new SimpleGrantedAuthority(createMember.getMemberRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createMember.getEmail(),
                createMember.getMemberRole()
        );
    }

    private Member getMember(OAuthAttributes attributes, MemberType memberType) {
        Member findMember = memberRepository.findByMemberTypeAndSocialId(memberType,
                attributes.getOAuth2UserInfo().getId()).orElse(null);

        if (findMember == null) {
            return saveUser(attributes, memberType);
        }

        return findMember;
    }


    private MemberType getMemberType(String registrationId) {
        return MemberType.GOOGLE;
    }

    @Transactional
    public Member saveUser(OAuthAttributes attributes, MemberType memberType) {
        Member createdMember = attributes.toEntity(memberType, attributes.getOAuth2UserInfo());

        OauthUser oauthUser = OauthUser.builder()
                .email(createdMember.getEmail())
                .provider(memberType.toString())
                .providerId(attributes.getOAuth2UserInfo().getId())
                .member(createdMember)
                .build();

        log.info("Saving OAuth member: {}", createdMember);
        OauthUser savedOauthUser = oauthUserRepository.save(oauthUser);
        log.info("Saved OAuth member: {}", savedOauthUser);

        log.info("Saving member: {}", createdMember);
        Member savedMember = memberRepository.save(createdMember);
        log.info("Saved member: {}", savedMember);

        return savedMember;
    }
}