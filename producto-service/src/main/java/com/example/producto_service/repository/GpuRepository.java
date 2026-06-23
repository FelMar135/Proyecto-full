package com.example.producto_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.producto_service.model.Gpu;

public interface GpuRepository extends JpaRepository<Gpu, Long> {

}