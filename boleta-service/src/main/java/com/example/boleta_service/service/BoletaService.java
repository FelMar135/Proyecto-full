package com.example.boleta_service.service;

import com.example.boleta_service.exception.BadRequestException;
import com.example.boleta_service.exception.ResourceNotFoundException;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class BoletaService {

    private static final Logger logger = LoggerFactory.getLogger(BoletaService.class);

    private final BoletaRepository boletaRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${orden.service.url}")
    private String ordenServiceUrl;

    public BoletaService(
            BoletaRepository boletaRepository,
            WebClient.Builder webClientBuilder
    ) {
        this.boletaRepository = boletaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Boleta guardar(Boleta boleta) {
        validarBoleta(boleta);

        logger.info("Intentando generar boleta para orden ID: {}", boleta.getOrdenId());

        validarUsuarioExiste(boleta.getUsuarioId());
        validarOrdenExiste(boleta.getOrdenId());

        calcularMontos(boleta);

        boleta.setFechaEmision(LocalDate.now());
        boleta.setNumeroBoleta("BOL-" + System.currentTimeMillis());

        logger.info("Boleta generada correctamente: {}", boleta.getNumeroBoleta());

        return boletaRepository.save(boleta);
    }

    public List<Boleta> listar() {
        logger.info("Listando todas las boletas");
        return boletaRepository.findAll();
    }

    public Boleta buscarPorId(Long id) {
        logger.info("Buscando boleta con ID: {}", id);

        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la boleta con ID: " + id));
    }

    public boolean existePorId(Long id) {
        logger.info("Verificando existencia de boleta con ID: {}", id);
        return boletaRepository.existsById(id);
    }

    public Boleta actualizar(Long id, Boleta boletaActualizada) {
        logger.info("Intentando actualizar boleta con ID: {}", id);

        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la boleta con ID: " + id));

        validarBoleta(boletaActualizada);

        validarUsuarioExiste(boletaActualizada.getUsuarioId());
        validarOrdenExiste(boletaActualizada.getOrdenId());

        boleta.setOrdenId(boletaActualizada.getOrdenId());
        boleta.setUsuarioId(boletaActualizada.getUsuarioId());
        boleta.setSubtotal(boletaActualizada.getSubtotal());

        calcularMontos(boleta);

        logger.info("Boleta actualizada correctamente con ID: {}", id);

        return boletaRepository.save(boleta);
    }

    public void eliminar(Long id) {
        logger.warn("Intentando eliminar boleta con ID: {}", id);

        if (!boletaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la boleta con ID: " + id);
        }

        boletaRepository.deleteById(id);

        logger.info("Boleta eliminada correctamente con ID: {}", id);
    }

    public List<Boleta> buscarPorUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new BadRequestException("El ID del usuario debe ser mayor a 0");
        }

        logger.info("Buscando boletas del usuario ID: {}", usuarioId);

        return boletaRepository.findByUsuarioId(usuarioId);
    }

    public List<Boleta> buscarPorOrdenId(Long ordenId) {
        if (ordenId == null || ordenId <= 0) {
            throw new BadRequestException("El ID de la orden debe ser mayor a 0");
        }

        logger.info("Buscando boletas de la orden ID: {}", ordenId);

        return boletaRepository.findByOrdenId(ordenId);
    }

    public Double totalCompradoPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new BadRequestException("El ID del usuario debe ser mayor a 0");
        }

        logger.info("Calculando total comprado por usuario ID: {}", usuarioId);

        Double total = boletaRepository.totalCompradoPorUsuario(usuarioId);

        if (total == null) {
            return 0.0;
        }

        return total;
    }

    private void validarBoleta(Boleta boleta) {
        if (boleta == null) {
            throw new BadRequestException("La boleta no puede ser nula");
        }

        if (boleta.getOrdenId() == null || boleta.getOrdenId() <= 0) {
            throw new BadRequestException("El ID de la orden debe ser mayor a 0");
        }

        if (boleta.getUsuarioId() == null || boleta.getUsuarioId() <= 0) {
            throw new BadRequestException("El ID del usuario debe ser mayor a 0");
        }

        if (boleta.getSubtotal() == null || boleta.getSubtotal() <= 0) {
            throw new BadRequestException("El subtotal debe ser mayor a 0");
        }
    }

    private void validarUsuarioExiste(Long usuarioId) {
        Boolean existeUsuario;

        try {
            existeUsuario = webClientBuilder.build()
                    .get()
                    .uri(usuarioServiceUrl + "/usuarios/" + usuarioId + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            throw new BadRequestException("No se pudo validar el usuario");
        }

        if (existeUsuario == null || !existeUsuario) {
            throw new BadRequestException("El usuario no existe");
        }
    }

    private void validarOrdenExiste(Long ordenId) {
        Boolean existeOrden;

        try {
            existeOrden = webClientBuilder.build()
                    .get()
                    .uri(ordenServiceUrl + "/ordenes/" + ordenId + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            throw new BadRequestException("No se pudo validar la orden");
        }

        if (existeOrden == null || !existeOrden) {
            throw new BadRequestException("La orden no existe");
        }
    }

    private void calcularMontos(Boleta boleta) {
        double iva = boleta.getSubtotal() * 0.19;
        double total = boleta.getSubtotal() + iva;

        boleta.setIva(iva);
        boleta.setTotal(total);
    }
}