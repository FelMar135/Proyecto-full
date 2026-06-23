package com.example.soporte_service.repository;

import com.example.soporte_service.model.Soporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Long> {
    List<Soporte> findByUsuarioId(Long usuarioId);
    List<Soporte> findByOrdenId(Long ordenId);
}