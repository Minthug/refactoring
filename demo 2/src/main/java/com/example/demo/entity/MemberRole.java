package com.example.demo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    GUEST("MEMBER_GUEST"), USER("MEMBER_USER"), ADMIN("MEMBER_ADMIN"), SOCIAL("MEMBER_SOCIAL");

    private final String key;
}
