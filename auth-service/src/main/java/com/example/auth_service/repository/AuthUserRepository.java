package com.example.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth_service.model.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    AuthUser findByEmail(String email);
}