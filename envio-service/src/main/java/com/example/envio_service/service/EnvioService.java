package com.example.envio_service.service;

import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.exception.BusinessException;
import com.example.envio_service.exception.RemoteServiceException;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final WebClient ordenWebClient;

    public List<EnvioDTO> obtenerTodos() {
        log.info("Obteniendo todos los envíos");

        return envioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public EnvioDTO obtenerPorId(Long id) {
        log.info("Buscando envío con ID: {}", id);

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un envío con ID: " + id));

        return convertirADTO(envio);
    }

    public boolean existePorId(Long id) {
        log.info("Validando existencia de envío con ID: {}", id);

        return envioRepository.existsById(id);
    }

    public EnvioDTO crear(EnvioDTO envioDTO) {
        log.info("Creando envío para orden ID: {}", envioDTO.getOrdenId());

        validarOrdenExiste(envioDTO.getOrdenId());

        if (envioDTO.getFechaEnvio() == null) {
            envioDTO.setFechaEnvio(LocalDateTime.now());
        }

        if (envioDTO.getFechaEntregaEstimada() == null) {
            envioDTO.setFechaEntregaEstimada(envioDTO.getFechaEnvio().plusDays(3));
        }

        if (envioDTO.getNumeroSeguimiento() == null || envioDTO.getNumeroSeguimiento().isBlank()) {
            envioDTO.setNumeroSeguimiento(generarNumeroSeguimiento());
        }

        Envio envio = convertirAEntidad(envioDTO);
        Envio envioGuardado = envioRepository.save(envio);

        return convertirADTO(envioGuardado);
    }

    public EnvioDTO actualizar(Long id, EnvioDTO envioDTO) {
        log.info("Actualizando envío con ID: {}", id);

        Envio envioExistente = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un envío con ID: " + id));

        validarOrdenExiste(envioDTO.getOrdenId());

        envioExistente.setOrdenId(envioDTO.getOrdenId());
        envioExistente.setDireccionEnvio(envioDTO.getDireccionEnvio());
        envioExistente.setComuna(envioDTO.getComuna());
        envioExistente.setCiudad(envioDTO.getCiudad());
        envioExistente.setEmpresaTransportista(envioDTO.getEmpresaTransportista());
        envioExistente.setEstado(envioDTO.getEstado());

        if (envioDTO.getNumeroSeguimiento() == null || envioDTO.getNumeroSeguimiento().isBlank()) {
            envioExistente.setNumeroSeguimiento(envioExistente.getNumeroSeguimiento());
        } else {
            envioExistente.setNumeroSeguimiento(envioDTO.getNumeroSeguimiento());
        }

        if (envioDTO.getFechaEnvio() == null) {
            envioExistente.setFechaEnvio(envioExistente.getFechaEnvio());
        } else {
            envioExistente.setFechaEnvio(envioDTO.getFechaEnvio());
        }

        if (envioDTO.getFechaEntregaEstimada() == null) {
            envioExistente.setFechaEntregaEstimada(envioExistente.getFechaEntregaEstimada());
        } else {
            envioExistente.setFechaEntregaEstimada(envioDTO.getFechaEntregaEstimada());
        }

        Envio envioActualizado = envioRepository.save(envioExistente);

        return convertirADTO(envioActualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando envío con ID: {}", id);

        if (!envioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un envío con ID: " + id);
        }

        envioRepository.deleteById(id);
    }

    public List<EnvioDTO> obtenerPorOrdenId(Long ordenId) {
        log.info("Obteniendo envíos asociados a la orden ID: {}", ordenId);

        return envioRepository.findByOrdenId(ordenId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<EnvioDTO> obtenerPorEstado(String estado) {
        log.info("Obteniendo envíos con estado: {}", estado);

        return envioRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<EnvioDTO> obtenerPorCiudad(String ciudad) {
        log.info("Obteniendo envíos de la ciudad: {}", ciudad);

        return envioRepository.findByCiudad(ciudad)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    private void validarOrdenExiste(Long ordenId) {
        try {
            log.info("Validando existencia de orden ID: {}", ordenId);

            Boolean existe = ordenWebClient.get()
                    .uri("/ordenes/{id}/exists", ordenId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (!Boolean.TRUE.equals(existe)) {
                throw new BusinessException("La orden con ID " + ordenId + " no existe");
            }

        } catch (WebClientResponseException.NotFound ex) {
            throw new BusinessException("La orden con ID " + ordenId + " no existe");

        } catch (WebClientResponseException ex) {
            throw new RemoteServiceException("Error al comunicarse con orden-service: " + ex.getMessage());

        } catch (BusinessException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo validar la orden en orden-service");
        }
    }

    private String generarNumeroSeguimiento() {
        return "ENV-" + System.currentTimeMillis();
    }

    private EnvioDTO convertirADTO(Envio envio) {
        return EnvioDTO.builder()
                .id(envio.getId())
                .ordenId(envio.getOrdenId())
                .direccionEnvio(envio.getDireccionEnvio())
                .comuna(envio.getComuna())
                .ciudad(envio.getCiudad())
                .empresaTransportista(envio.getEmpresaTransportista())
                .numeroSeguimiento(envio.getNumeroSeguimiento())
                .estado(envio.getEstado())
                .fechaEnvio(envio.getFechaEnvio())
                .fechaEntregaEstimada(envio.getFechaEntregaEstimada())
                .build();
    }

    private Envio convertirAEntidad(EnvioDTO envioDTO) {
        return Envio.builder()
                .id(envioDTO.getId())
                .ordenId(envioDTO.getOrdenId())
                .direccionEnvio(envioDTO.getDireccionEnvio())
                .comuna(envioDTO.getComuna())
                .ciudad(envioDTO.getCiudad())
                .empresaTransportista(envioDTO.getEmpresaTransportista())
                .numeroSeguimiento(envioDTO.getNumeroSeguimiento())
                .estado(envioDTO.getEstado())
                .fechaEnvio(envioDTO.getFechaEnvio())
                .fechaEntregaEstimada(envioDTO.getFechaEntregaEstimada())
                .build();
    }
}