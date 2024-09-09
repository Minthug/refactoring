package com.example.demo.entity;

import com.example.demo.config.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String content;

    public Comment(Diary diary, Member member, String content) {
        this.diary = diary;
        this.member = member;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
