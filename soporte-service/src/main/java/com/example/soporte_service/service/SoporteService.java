package com.example.soporte_service.service;

import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.exception.BadRequestException;
import com.example.soporte_service.exception.ResourceNotFoundException;
import com.example.soporte_service.model.Soporte;
import com.example.soporte_service.repository.SoporteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SoporteService {

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
        return soporteRepository.findAll().stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }

    public SoporteDTO findById(Long id) {
        Soporte soporte = soporteRepository.findById(id)
                // AQUI: Cambiado a ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id));
        return SoporteDTO.fromModel(soporte);
    }

    public SoporteDTO save(SoporteDTO dto) {
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri(usuarioServiceUrl + "/usuarios/" + dto.getUsuarioId() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(userExists)) {
            // AQUI: Cambiado a BadRequestException
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
                // AQUI: Cambiado a BadRequestException
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
        return SoporteDTO.fromModel(soporteGuardado);
    }

    public SoporteDTO update(Long id, SoporteDTO dto) {
        Soporte existente = soporteRepository.findById(id)
                // AQUI: Cambiado a ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id));

        existente.setAsunto(dto.getAsunto());
        existente.setDescripcion(dto.getDescripcion());
        existente.setEstado(dto.getEstado());

        Soporte soporteActualizado = soporteRepository.save(existente);
        return SoporteDTO.fromModel(soporteActualizado);
    }

    public void deleteById(Long id) {
        if (!soporteRepository.existsById(id)) {
            // AQUI: Cambiado a ResourceNotFoundException
            throw new ResourceNotFoundException("Ticket de soporte no encontrado con ID: " + id);
        }
        soporteRepository.deleteById(id);
    }

    public List<SoporteDTO> findByUsuarioId(Long usuarioId) {
        return soporteRepository.findByUsuarioId(usuarioId).stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }

    public List<SoporteDTO> findByOrdenId(Long ordenId) {
        return soporteRepository.findByOrdenId(ordenId).stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
    }
}