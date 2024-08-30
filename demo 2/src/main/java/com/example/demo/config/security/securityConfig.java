package com.example.demo.config.security;

import com.example.demo.config.jwt.service.JwtService;
import com.example.demo.config.jwt.service.LoginService;
import com.example.demo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class securityConfig {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final ObjectMapper mapper;
    private final LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(configurationSource()).and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .requestMatchers().permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .loginPage("")
                .successHandler()
                .failureHandler()
                .userInfoEndpoint().userService();

        http.addFilterAfter();
        http.addFilterBefore();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "refreshToken"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}