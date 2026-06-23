package com.example.orden_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.orden_service.model.Orden;
import com.example.orden_service.repository.OrdenRepository;

import java.util.List;

@Service
public class OrdenService {

    private static final Logger logger = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;
    private final RestTemplate restTemplate;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${carrito.service.url}")
    private String carritoServiceUrl;

    public OrdenService(OrdenRepository ordenRepository, RestTemplate restTemplate) {
        this.ordenRepository = ordenRepository;
        this.restTemplate = restTemplate;
    }

    public Orden guardar(Orden orden) {

        logger.info("Intentando crear orden para usuario ID: {}", orden.getUsuarioId());

        Boolean existeUsuario = restTemplate.getForObject(
                usuarioServiceUrl + "/usuarios/" + orden.getUsuarioId() + "/exists",
                Boolean.class
        );

        if (existeUsuario == null || !existeUsuario) {
            logger.error("No se pudo crear la orden. Usuario no existe con ID: {}", orden.getUsuarioId());
            throw new RuntimeException("El usuario no existe");
        }

        Boolean existeCarrito = restTemplate.getForObject(
                carritoServiceUrl + "/carritos/" + orden.getCarritoId() + "/exists",
                Boolean.class
        );

        if (existeCarrito == null || !existeCarrito) {
            logger.error("No se pudo crear la orden. Carrito no existe con ID: {}", orden.getCarritoId());
            throw new RuntimeException("El carrito no existe");
        }

        logger.info("Orden creada correctamente para usuario ID: {}", orden.getUsuarioId());

        return ordenRepository.save(orden);
    }

    public List<Orden> listar() {
        logger.info("Listando todas las órdenes");
        return ordenRepository.findAll();
    }

    public Orden buscarPorId(Long id) {
        logger.info("Buscando orden con ID: {}", id);
        return ordenRepository.findById(id).orElse(null);
    }

    public boolean existePorId(Long id) {
        logger.info("Verificando existencia de orden con ID: {}", id);
        return ordenRepository.existsById(id);
    }

    public Orden actualizar(Long id, Orden ordenActualizada) {

        logger.info("Intentando actualizar orden con ID: {}", id);

        Orden orden = ordenRepository.findById(id).orElse(null);

        if (orden == null) {
            logger.warn("No se encontró orden con ID: {}", id);
            return null;
        }

        Boolean existeUsuario = restTemplate.getForObject(
                usuarioServiceUrl + "/usuarios/" + ordenActualizada.getUsuarioId() + "/exists",
                Boolean.class
        );

        if (existeUsuario == null || !existeUsuario) {
            logger.error("No se pudo actualizar la orden. Usuario no existe con ID: {}", ordenActualizada.getUsuarioId());
            throw new RuntimeException("El usuario no existe");
        }

        Boolean existeCarrito = restTemplate.getForObject(
                carritoServiceUrl + "/carritos/" + ordenActualizada.getCarritoId() + "/exists",
                Boolean.class
        );

        if (existeCarrito == null || !existeCarrito) {
            logger.error("No se pudo actualizar la orden. Carrito no existe con ID: {}", ordenActualizada.getCarritoId());
            throw new RuntimeException("El carrito no existe");
        }

        orden.setUsuarioId(ordenActualizada.getUsuarioId());
        orden.setCarritoId(ordenActualizada.getCarritoId());
        orden.setTotal(ordenActualizada.getTotal());
        orden.setEstado(ordenActualizada.getEstado());

        logger.info("Orden actualizada correctamente con ID: {}", id);

        return ordenRepository.save(orden);
    }

    public boolean eliminar(Long id) {

        logger.warn("Intentando eliminar orden con ID: {}", id);

        if (!ordenRepository.existsById(id)) {
            logger.error("No se pudo eliminar. Orden no encontrada con ID: {}", id);
            return false;
        }

        ordenRepository.deleteById(id);

        logger.info("Orden eliminada correctamente con ID: {}", id);

        return true;
    }

    public List<Orden> buscarPorUsuarioId(Long usuarioId) {
        logger.info("Buscando órdenes del usuario ID: {}", usuarioId);
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    public Double totalVentas() {
        logger.info("Calculando total de ventas");
        return ordenRepository.totalVentas();
    }
}