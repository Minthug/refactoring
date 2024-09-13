package com.example.demo.config.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleOAuthToken {

    private String accessToken;
    private int expiresIn;
    private String scope;
    private String tokenType;
    private String idToken;
    private String jwtToken;
}
