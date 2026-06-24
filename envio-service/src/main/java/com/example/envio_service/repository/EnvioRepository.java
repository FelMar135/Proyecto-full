package com.example.envio_service.repository;

import com.example.envio_service.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    List<Envio> findByOrdenId(Long ordenId);

    List<Envio> findByEstado(String estado);

    List<Envio> findByCiudad(String ciudad);
}