package com.example.carrito_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.carrito_service.model.Carrito;

import java.util.List;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(c.cantidad), 0) FROM Carrito c WHERE c.usuarioId = :usuarioId")
    Integer totalProductosPorUsuario(@Param("usuarioId") Long usuarioId);
}