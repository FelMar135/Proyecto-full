package com.example.resena_service.service;

import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.model.Resena;
import com.example.resena_service.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final WebClient.Builder webClientBuilder;

    // Inyectamos las URL comuinicacion
    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    public ResenaService(ResenaRepository resenaRepository, WebClient.Builder webClientBuilder) {
        this.resenaRepository = resenaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<ResenaDTO> findAll() {
        return resenaRepository.findAll().stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }

    public ResenaDTO findById(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));
        return ResenaDTO.fromModel(resena);
    }

    public Boolean existsById(Long id) {
        return resenaRepository.existsById(id);
    }

    public ResenaDTO save(ResenaDTO dto) {
        // 1. Validar usando la URL del application.properties
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri(usuarioServiceUrl + "/usuarios/" + dto.getUsuarioId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        // 2. Validar la GPU usando su respectiva URL
        Boolean gpuExists = webClientBuilder.build()
                .get()
                .uri(productoServiceUrl + "/gpus/" + dto.getGpuId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(userExists) || Boolean.FALSE.equals(gpuExists)) {
            throw new RuntimeException("Error: El usuario o la GPU no existen en la base de datos.");
        }

        // 3. Convertir a Modelo, Guardar y retornar DTO
        Resena resena = dto.toModel();
        Resena resenaGuardada = resenaRepository.save(resena);
        return ResenaDTO.fromModel(resenaGuardada);
    }

    public ResenaDTO update(Long id, ResenaDTO dto) {
        Resena existente = resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));

        existente.setComentario(dto.getComentario());
        existente.setCalificacion(dto.getCalificacion());
        existente.setFecha(dto.getFecha());

        Resena resenaActualizada = resenaRepository.save(existente);
        return ResenaDTO.fromModel(resenaActualizada);
    }

    public void deleteById(Long id) {
        if (!resenaRepository.existsById(id)) {
            throw new RuntimeException("Reseña no encontrada con ID: " + id);
        }
        resenaRepository.deleteById(id);
    }

    public List<ResenaDTO> findByUsuarioId(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }

    public List<ResenaDTO> findByGpuId(Long gpuId) {
        return resenaRepository.findByGpuId(gpuId).stream()
                .map(ResenaDTO::fromModel)
                .collect(Collectors.toList());
    }
}