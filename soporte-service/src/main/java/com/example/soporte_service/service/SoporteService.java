package com.example.soporte_service.service;

import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.exception.BadRequestException;
import com.example.soporte_service.exception.ResourceNotFoundException;
import com.example.soporte_service.model.Soporte;
import com.example.soporte_service.repository.SoporteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SoporteService {

    private static final Logger logger =
            LoggerFactory.getLogger(SoporteService.class);

    private final SoporteRepository soporteRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${orden.service.url}")
    private String ordenServiceUrl;

    public SoporteService(SoporteRepository soporteRepository, WebClient.Builder webClientBuilder) {
        this.soporteRepository = soporteRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<SoporteDTO> findAll() {
        logger.info("Listando todos los tickets de soporte");

        return soporteRepository.findAll().stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }

    public SoporteDTO findById(Long id) {
        logger.info("Buscando ticket de soporte con ID: {}", id);

        Soporte soporte = soporteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ticket de soporte no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id);
                });

        return SoporteDTO.fromModel(soporte);
    }

    public SoporteDTO save(SoporteDTO dto) {
        logger.info("Creando ticket de soporte para usuario ID: {}", dto.getUsuarioId());

        Boolean userExists = webClientBuilder.build()
                .get()
                .uri(usuarioServiceUrl + "/usuarios/" + dto.getUsuarioId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(userExists)) {
            logger.error("El usuario {} no existe", dto.getUsuarioId());
            throw new BadRequestException("Error: El usuario indicado no existe.");
        }

        if (dto.getOrdenId() != null) {
            Boolean ordenExists = webClientBuilder.build()
                    .get()
                    .uri(ordenServiceUrl + "/ordenes/" + dto.getOrdenId() + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (Boolean.FALSE.equals(ordenExists)) {
                logger.error("La orden {} no existe", dto.getOrdenId());
                throw new BadRequestException("Error: La orden indicada no existe en la base de datos.");
            }
        }

        if (dto.getFechaCreacion() == null) {
            dto.setFechaCreacion(LocalDate.now());
        }

        if (dto.getEstado() == null || dto.getEstado().trim().isEmpty()) {
            dto.setEstado("ABIERTO");
        }

        Soporte soporte = dto.toModel();
        Soporte soporteGuardado = soporteRepository.save(soporte);

        logger.info("Ticket de soporte creado correctamente con ID: {}", soporteGuardado.getId());
        return SoporteDTO.fromModel(soporteGuardado);
    }

        public SoporteDTO update(Long id, SoporteDTO dto) {
        logger.info("Actualizando ticket de soporte con ID: {}", id);

        Soporte existente = soporteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ticket de soporte no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id);
                });

        existente.setAsunto(dto.getAsunto());
        existente.setDescripcion(dto.getDescripcion());
        existente.setEstado(dto.getEstado());

        Soporte soporteActualizado = soporteRepository.save(existente);

        logger.info("Ticket actualizado correctamente con ID: {}", id);
        return SoporteDTO.fromModel(soporteActualizado);
    }

    public Boolean existsById(Long id) {
        logger.info("Verificando existencia del ticket con ID: {}", id);
        return soporteRepository.existsById(id);
    }

    public void deleteById(Long id) {
        logger.warn("Intentando eliminar ticket de soporte con ID: {}", id);

        if (!soporteRepository.existsById(id)) {
            logger.error("Ticket de soporte no encontrado con ID: {}", id);
            throw new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id);
        }

        soporteRepository.deleteById(id);

        logger.info("Ticket eliminado correctamente con ID: {}", id);
    }

    public List<SoporteDTO> findByUsuarioId(Long usuarioId) {
        logger.info("Buscando tickets del usuario ID: {}", usuarioId);

        return soporteRepository.findByUsuarioId(usuarioId).stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }

    public List<SoporteDTO> findByOrdenId(Long ordenId) {
        logger.info("Buscando tickets de la orden ID: {}", ordenId);

        return soporteRepository.findByOrdenId(ordenId).stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }
}