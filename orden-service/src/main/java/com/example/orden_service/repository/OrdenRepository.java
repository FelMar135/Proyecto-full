package com.example.orden_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.orden_service.model.Orden;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuarioId(Long usuarioId);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Orden o")
    Double totalVentas();
}