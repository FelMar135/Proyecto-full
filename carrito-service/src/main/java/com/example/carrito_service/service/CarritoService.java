package com.example.carrito_service.service;

import com.example.carrito_service.exception.BadRequestException;
import com.example.carrito_service.exception.ResourceNotFoundException;
import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.repository.CarritoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final WebClient webClient;

    // Traemos las URLs desde el application.properties
    @Value("${producto.service.url}")
    private String productoServiceUrl;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public CarritoService(CarritoRepository carritoRepository, WebClient.Builder webClientBuilder) {
        this.carritoRepository = carritoRepository;
        this.webClient = webClientBuilder.build();
    }

    public Carrito guardar(Carrito carrito) {
        log.info("Iniciando guardado en carrito. Validando Usuario ID: {} y GPU ID: {}", carrito.getUsuarioId(), carrito.getGpuId());
        validarUsuarioYGpu(carrito.getUsuarioId(), carrito.getGpuId());

        Carrito guardado = carritoRepository.save(carrito);
        log.info("Producto agregado al carrito con éxito. Carrito ID: {}", guardado.getId());
        return guardado;
    }

    public List<Carrito> listar() {
        log.info("Obteniendo todos los registros de carritos");
        return carritoRepository.findAll();
    }

    public Carrito buscarPorId(Long id) {
        log.info("Buscando carrito con ID: {}", id);
        return carritoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo al buscar: Carrito no encontrado con ID {}", id);
                    return new ResourceNotFoundException("Registro de carrito no encontrado con ID: " + id);
                });
    }

    public boolean existePorId(Long id) {
        return carritoRepository.existsById(id);
    }

    public Carrito actualizar(Long id, Carrito carritoActualizado) {
        log.info("Intentando actualizar carrito con ID: {}", id);

        Carrito carritoExistente = buscarPorId(id); // Reutilizamos el método que ya lanza la excepción

        // Si cambiaron el usuario o la gpu, volvemos a validar que existan
        if (!carritoExistente.getUsuarioId().equals(carritoActualizado.getUsuarioId()) || 
            !carritoExistente.getGpuId().equals(carritoActualizado.getGpuId())) {
            validarUsuarioYGpu(carritoActualizado.getUsuarioId(), carritoActualizado.getGpuId());
        }

        carritoExistente.setUsuarioId(carritoActualizado.getUsuarioId());
        carritoExistente.setGpuId(carritoActualizado.getGpuId());
        carritoExistente.setCantidad(carritoActualizado.getCantidad());

        Carrito actualizado = carritoRepository.save(carritoExistente);
        log.info("Carrito actualizado correctamente con ID: {}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.warn("Solicitud para eliminar registro de carrito con ID: {}", id);
        if (!carritoRepository.existsById(id)) {
            log.error("Fallo al eliminar: Carrito no encontrado con ID {}", id);
            throw new ResourceNotFoundException("Registro de carrito no encontrado con ID: " + id);
        }

        carritoRepository.deleteById(id);
        log.info("Registro de carrito eliminado correctamente. ID: {}", id);
    }

    public List<Carrito> buscarPorUsuarioId(Long usuarioId) {
        log.info("Buscando todos los items en el carrito del Usuario ID: {}", usuarioId);
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    public Integer totalProductosPorUsuario(Long usuarioId) {
        log.info("Calculando total de productos para el Usuario ID: {}", usuarioId);
        Integer total = carritoRepository.totalProductosPorUsuario(usuarioId);
        return total != null ? total : 0;
    }

    // --- MÉTODO PRIVADO PARA REUTILIZAR LA LÓGICA DE WEBCLIENT ---
    private void validarUsuarioYGpu(Long usuarioId, Long gpuId) {
        // 1. Validar Usuario
        Boolean existeUsuario = webClient.get()
                .uri(usuarioServiceUrl + "/usuarios/" + usuarioId + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(existeUsuario)) {
            log.error("Validación fallida: El usuario ID {} no existe en user-service", usuarioId);
            throw new BadRequestException("El usuario especificado no existe.");
        }

        // 2. Validar GPU
        Boolean existeGpu = webClient.get()
                .uri(productoServiceUrl + "/gpus/" + gpuId + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(existeGpu)) {
            log.error("Validación fallida: La GPU ID {} no existe en producto-service", gpuId);
            throw new BadRequestException("La GPU especificada no existe.");
        }
    }
}