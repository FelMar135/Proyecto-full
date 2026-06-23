package com.example.boleta_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.boleta_service.model.Boleta;

import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findByUsuarioId(Long usuarioId);

    List<Boleta> findByOrdenId(Long ordenId);

    @Query("SELECT COALESCE(SUM(b.total), 0) FROM Boleta b WHERE b.usuarioId = :usuarioId")
    Double totalCompradoPorUsuario(@Param("usuarioId") Long usuarioId);
}