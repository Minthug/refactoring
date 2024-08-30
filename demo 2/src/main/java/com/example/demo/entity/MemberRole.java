package com.example.demo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    GUEST, USER, ADMIN, SOCIAL;

}
