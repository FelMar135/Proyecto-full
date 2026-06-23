package com.example.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}