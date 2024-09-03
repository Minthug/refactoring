package com.example.demo.dto.auth;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDto {
    private String email;
    private String password;

    public Member toMember(PasswordEncoder encoder) {
        return Member.builder()
                .email(email)
                .password(password)
                .memberRole(MemberRole.USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
