package com.example.demo.entity;

import com.example.demo.config.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OauthUser extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String provider;

    private String providerId;

    private String name;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
