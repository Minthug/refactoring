package com.example.demo.repository;

import com.example.demo.entity.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthUserRepository extends JpaRepository<OauthUser, Long> {
}
