package com.example.orden_service.service;

import com.example.orden_service.exception.BadRequestException;
import com.example.orden_service.exception.ResourceNotFoundException;
import com.example.orden_service.model.Orden;
import com.example.orden_service.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${carrito.service.url}")
    private String carritoServiceUrl;

    public Orden guardar(Orden orden) {
        log.info("Creando orden para usuario ID: {}", orden.getUsuarioId());

        validarTotalOrden(orden.getTotal());
        validarUsuarioExiste(orden.getUsuarioId());
        validarCarritoExiste(orden.getCarritoId());

        return ordenRepository.save(orden);
    }

    public List<Orden> listar() {
        log.info("Listando todas las órdenes");

        return ordenRepository.findAll();
    }

    public Orden buscarPorId(Long id) {
        log.info("Buscando orden con ID: {}", id);

        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una orden con ID: " + id));
    }

    public boolean existePorId(Long id) {
        log.info("Verificando existencia de orden con ID: {}", id);

        return ordenRepository.existsById(id);
    }

    public Orden actualizar(Long id, Orden ordenActualizada) {
        log.info("Actualizando orden con ID: {}", id);

        Orden ordenExistente = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una orden con ID: " + id));

        validarTotalOrden(ordenActualizada.getTotal());
        validarUsuarioExiste(ordenActualizada.getUsuarioId());
        validarCarritoExiste(ordenActualizada.getCarritoId());

        ordenExistente.setUsuarioId(ordenActualizada.getUsuarioId());
        ordenExistente.setCarritoId(ordenActualizada.getCarritoId());
        ordenExistente.setTotal(ordenActualizada.getTotal());
        ordenExistente.setEstado(ordenActualizada.getEstado());

        return ordenRepository.save(ordenExistente);
    }

    public void eliminar(Long id) {
        log.info("Eliminando orden con ID: {}", id);

        if (!ordenRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe una orden con ID: " + id);
        }

        ordenRepository.deleteById(id);
    }

    public List<Orden> buscarPorUsuarioId(Long usuarioId) {
        log.info("Buscando órdenes del usuario ID: {}", usuarioId);

        return ordenRepository.findByUsuarioId(usuarioId);
    }

    public Double totalVentas() {
        log.info("Calculando total de ventas");

        Double total = ordenRepository.totalVentas();

        if (total == null) {
            return 0.0;
        }

        return total;
    }

    public void validarTotalOrden(Double total) {
        if (total == null || total <= 0) {
            throw new BadRequestException("El total de la orden debe ser mayor a 0");
        }
    }

    private void validarUsuarioExiste(Long usuarioId) {
        try {
            log.info("Validando existencia de usuario ID: {}", usuarioId);

            Boolean existeUsuario = webClientBuilder.build()
                    .get()
                    .uri(usuarioServiceUrl + "/usuarios/" + usuarioId + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (!Boolean.TRUE.equals(existeUsuario)) {
                throw new BadRequestException("El usuario con ID " + usuarioId + " no existe");
            }

        } catch (BadRequestException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new BadRequestException("No se pudo validar el usuario en usuario-service");
        }
    }

    private void validarCarritoExiste(Long carritoId) {
        try {
            log.info("Validando existencia de carrito ID: {}", carritoId);

            Boolean existeCarrito = webClientBuilder.build()
                    .get()
                    .uri(carritoServiceUrl + "/carritos/" + carritoId + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (!Boolean.TRUE.equals(existeCarrito)) {
                throw new BadRequestException("El carrito con ID " + carritoId + " no existe");
            }

        } catch (BadRequestException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new BadRequestException("No se pudo validar el carrito en carrito-service");
        }
    }
}