package com.example.pago_service.repository;

import com.example.pago_service.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByOrdenId(Long ordenId);

    List<Pago> findByEstadoIgnoreCase(String estado);
}