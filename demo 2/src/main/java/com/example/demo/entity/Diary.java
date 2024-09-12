package com.example.demo.entity;

import com.example.demo.config.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Diary extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subTitle;

    private String content;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Diary(String title, String subTitle, String content, Member member, List<MultipartFile> imageFiles) {
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.member = member;
        this.images = new ArrayList<>();
    }

    public void update(String title, String subTitle, String content) {
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addImages(Image image) {
        this.images.add(image);
    }
}
