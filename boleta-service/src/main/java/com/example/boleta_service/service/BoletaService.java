package com.example.boleta_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.boleta_service.exception.BadRequestException;
import com.example.boleta_service.exception.ResourceNotFoundException;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;

@Service
public class BoletaService {

    private static final Logger logger =
            LoggerFactory.getLogger(BoletaService.class);

    private final BoletaRepository boletaRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${orden.service.url}")
    private String ordenServiceUrl;

    public BoletaService(
            BoletaRepository boletaRepository,
            WebClient.Builder webClientBuilder) {

        this.boletaRepository = boletaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Boleta guardar(Boleta boleta) {

        logger.info("Intentando generar boleta para orden ID: {}", boleta.getOrdenId());

        Boolean existeUsuario = webClientBuilder.build()
                .get()
                .uri(usuarioServiceUrl +
                        "/usuarios/" +
                        boleta.getUsuarioId() +
                        "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (existeUsuario == null || !existeUsuario) {

            logger.error(
                    "No se pudo generar la boleta. Usuario no existe con ID: {}",
                    boleta.getUsuarioId());

            throw new BadRequestException("El usuario no existe");
        }

        Boolean existeOrden = webClientBuilder.build()
                .get()
                .uri(ordenServiceUrl +
                        "/ordenes/" +
                        boleta.getOrdenId() +
                        "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (existeOrden == null || !existeOrden) {

            logger.error(
                    "No se pudo generar la boleta. Orden no existe con ID: {}",
                    boleta.getOrdenId());

            throw new BadRequestException("La orden no existe");
        }

        // Regla de negocio

        boleta.setIva(
                boleta.getSubtotal() * 0.19);

        boleta.setTotal(
                boleta.getSubtotal() +
                boleta.getIva());

        boleta.setFechaEmision(
                LocalDate.now());

        boleta.setNumeroBoleta(
                "BOL-" + System.currentTimeMillis());

        logger.info(
                "Boleta generada correctamente: {}",
                boleta.getNumeroBoleta());

        return boletaRepository.save(boleta);
    }

    public List<Boleta> listar() {

        logger.info("Listando todas las boletas");

        return boletaRepository.findAll();
    }

    public Boleta buscarPorId(Long id) {

        logger.info("Buscando boleta con ID: {}", id);

        return boletaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la boleta con ID: " + id));
    }

    public boolean existePorId(Long id) {

        logger.info("Verificando existencia de boleta con ID: {}", id);

        return boletaRepository.existsById(id);
    }

    public Boleta actualizar(Long id, Boleta boletaActualizada) {

        logger.info("Intentando actualizar boleta con ID: {}", id);

        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la boleta con ID: " + id));

        boleta.setOrdenId(
                boletaActualizada.getOrdenId());

        boleta.setUsuarioId(
                boletaActualizada.getUsuarioId());

        boleta.setSubtotal(
                boletaActualizada.getSubtotal());

        boleta.setIva(
                boletaActualizada.getSubtotal() * 0.19);

        boleta.setTotal(
                boletaActualizada.getSubtotal() +
                boleta.getIva());

        logger.info(
                "Boleta actualizada correctamente con ID: {}",
                id);

        return boletaRepository.save(boleta);
    }

    public boolean eliminar(Long id) {

        logger.warn(
                "Intentando eliminar boleta con ID: {}",
                id);

        if (!boletaRepository.existsById(id)) {

            logger.error(
                    "No se pudo eliminar. Boleta no encontrada con ID: {}",
                    id);

            throw new ResourceNotFoundException(
                    "No se encontró la boleta con ID: " + id);
        }

        boletaRepository.deleteById(id);

        logger.info(
                "Boleta eliminada correctamente con ID: {}",
                id);

        return true;
    }

    public List<Boleta> buscarPorUsuarioId(Long usuarioId) {

        logger.info(
                "Buscando boletas del usuario ID: {}",
                usuarioId);

        return boletaRepository.findByUsuarioId(usuarioId);
    }

    public List<Boleta> buscarPorOrdenId(Long ordenId) {

        logger.info(
                "Buscando boletas de la orden ID: {}",
                ordenId);

        return boletaRepository.findByOrdenId(ordenId);
    }

    public Double totalCompradoPorUsuario(Long usuarioId) {

        logger.info(
                "Calculando total comprado por usuario ID: {}",
                usuarioId);

        return boletaRepository.totalCompradoPorUsuario(usuarioId);
    }
}