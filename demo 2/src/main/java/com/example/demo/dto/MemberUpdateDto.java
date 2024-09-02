package com.example.demo.dto;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberUpdateDto {
    private String nickname;
    private String introduce;
    private String password;
    private List<MultipartFile> imagesFiles = new ArrayList<>();
    private List<Long> imageId;
}
