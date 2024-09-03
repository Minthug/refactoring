package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotNull(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    private String introduce;

    private String imageUrl;

}
