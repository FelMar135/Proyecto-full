package com.example.pago_service.service;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.exception.BadRequestException;
import com.example.pago_service.exception.ResourceNotFoundException;
import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
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
public class PagoService {

    private final PagoRepository pagoRepository;
    private final WebClient ordenWebClient;

    public List<PagoDTO> obtenerTodos() {
        log.info("Obteniendo todos los pagos");

        return pagoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public PagoDTO obtenerPorId(Long id) {
        log.info("Buscando pago con ID: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un pago con ID: " + id));

        return convertirADTO(pago);
    }

    public boolean existePorId(Long id) {
        log.info("Validando existencia de pago con ID: {}", id);

        return pagoRepository.existsById(id);
    }

    public PagoDTO crear(PagoDTO pagoDTO) {
        log.info("Creando pago para orden ID: {}", pagoDTO.getOrdenId());

        validarOrdenExiste(pagoDTO.getOrdenId());

        if (pagoDTO.getFechaPago() == null) {
            pagoDTO.setFechaPago(LocalDateTime.now());
        }

        Pago pago = convertirAEntidad(pagoDTO);
        Pago pagoGuardado = pagoRepository.save(pago);

        return convertirADTO(pagoGuardado);
    }

    public PagoDTO actualizar(Long id, PagoDTO pagoDTO) {
        log.info("Actualizando pago con ID: {}", id);

        Pago pagoExistente = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un pago con ID: " + id));

        validarOrdenExiste(pagoDTO.getOrdenId());

        pagoExistente.setOrdenId(pagoDTO.getOrdenId());
        pagoExistente.setMonto(pagoDTO.getMonto());
        pagoExistente.setMetodoPago(pagoDTO.getMetodoPago());
        pagoExistente.setEstado(pagoDTO.getEstado());

        if (pagoDTO.getFechaPago() == null) {
            pagoExistente.setFechaPago(LocalDateTime.now());
        } else {
            pagoExistente.setFechaPago(pagoDTO.getFechaPago());
        }

        Pago pagoActualizado = pagoRepository.save(pagoExistente);

        return convertirADTO(pagoActualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando pago con ID: {}", id);

        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un pago con ID: " + id);
        }

        pagoRepository.deleteById(id);
    }

    public List<PagoDTO> obtenerPorOrdenId(Long ordenId) {
        log.info("Obteniendo pagos asociados a la orden ID: {}", ordenId);

        return pagoRepository.findByOrdenId(ordenId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Double calcularIva(Double monto) {
        if (monto == null || monto <= 0) {
            throw new BadRequestException("El monto debe ser mayor a 0 para calcular IVA");
        }

        return monto * 0.19;
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
                throw new BadRequestException("La orden con ID " + ordenId + " no existe");
            }

        } catch (WebClientResponseException.NotFound ex) {
            throw new BadRequestException("La orden con ID " + ordenId + " no existe");

        } catch (WebClientResponseException ex) {
            throw new BadRequestException("Error al comunicarse con orden-service: " + ex.getMessage());

        } catch (BadRequestException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new BadRequestException("No se pudo validar la orden en orden-service");
        }
    }

    private PagoDTO convertirADTO(Pago pago) {
        return PagoDTO.builder()
                .id(pago.getId())
                .ordenId(pago.getOrdenId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private Pago convertirAEntidad(PagoDTO pagoDTO) {
        return Pago.builder()
                .id(pagoDTO.getId())
                .ordenId(pagoDTO.getOrdenId())
                .monto(pagoDTO.getMonto())
                .metodoPago(pagoDTO.getMetodoPago())
                .estado(pagoDTO.getEstado())
                .fechaPago(pagoDTO.getFechaPago())
                .build();
    }
}