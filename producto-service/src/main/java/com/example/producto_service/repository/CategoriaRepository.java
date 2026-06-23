package com.example.producto_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.producto_service.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}