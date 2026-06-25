package com.example.resena_service.service;

import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.exception.BadRequestException;
import com.example.resena_service.exception.ResourceNotFoundException;
import com.example.resena_service.model.Resena;
import com.example.resena_service.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Value;

import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.exception.BadRequestException;
import com.example.resena_service.exception.ResourceNotFoundException;
import com.example.resena_service.model.Resena;
import com.example.resena_service.repository.ResenaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j // 'log'
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    public ResenaService(ResenaRepository resenaRepository, WebClient.Builder webClientBuilder) {
        this.resenaRepository = resenaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<ResenaDTO> findAll() {
        log.info("Obteniendo todas las reseñas de la base de datos.");
        return resenaRepository.findAll().stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }

    public ResenaDTO findById(Long id) {
        log.info("Buscando reseña con ID: {}", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo al buscar: Reseña no encontrada con ID {}", id);
                    return new ResourceNotFoundException("Reseña no encontrada con ID: " + id);
                });
        return ResenaDTO.fromModel(resena);
    }

    public Boolean existsById(Long id) {
        return resenaRepository.existsById(id);
    }

    public ResenaDTO save(ResenaDTO dto) {
        log.info("Iniciando validación para crear reseña. Usuario ID: {}, GPU ID: {}", dto.getUsuarioId(), dto.getGpuId());

        Boolean userExists = webClientBuilder.build()
                .get()
                .uri(usuarioServiceUrl + "/usuarios/" + dto.getUsuarioId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        Boolean gpuExists = webClientBuilder.build()
                .get()
                .uri(productoServiceUrl + "/gpus/" + dto.getGpuId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(userExists) || Boolean.FALSE.equals(gpuExists)) {
            log.error("Validación fallida al crear reseña. Usuario existe: {}, GPU existe: {}", userExists, gpuExists);
            throw new BadRequestException("Error: El usuario o la GPU no existen en la base de datos.");
        }

        Resena resena = dto.toModel();
        Resena resenaGuardada = resenaRepository.save(resena);
        log.info("Reseña creada exitosamente con ID: {}", resenaGuardada.getId());
        
        return ResenaDTO.fromModel(resenaGuardada);
    }

    public ResenaDTO update(Long id, ResenaDTO dto) {
        log.info("Intentando actualizar reseña con ID: {}", id);
        Resena existente = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo al actualizar: Reseña no encontrada con ID {}", id);
                    return new ResourceNotFoundException("Reseña no encontrada con ID: " + id);
                });

        existente.setComentario(dto.getComentario());
        existente.setCalificacion(dto.getCalificacion());
        existente.setFecha(dto.getFecha());

        Resena resenaActualizada = resenaRepository.save(existente);
        log.info("Reseña actualizada correctamente con ID: {}", id);
        
        return ResenaDTO.fromModel(resenaActualizada);
    }

    public void deleteById(Long id) {
        log.warn("Solicitud para eliminar reseña con ID: {}", id);
        if (!resenaRepository.existsById(id)) {
            log.error("Fallo al eliminar: Reseña no encontrada con ID {}", id);
            throw new ResourceNotFoundException("Reseña no encontrada con ID: " + id);
        }
        resenaRepository.deleteById(id);
        log.info("Reseña eliminada correctamente. ID: {}", id);
    }

    public List<ResenaDTO> findByUsuarioId(Long usuarioId) {
        log.info("Buscando reseñas pertenecientes al usuario ID: {}", usuarioId);
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }

    public List<ResenaDTO> findByGpuId(Long gpuId) {
        log.info("Buscando reseñas pertenecientes a la GPU ID: {}", gpuId);
        return resenaRepository.findByGpuId(gpuId).stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }
}