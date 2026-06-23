package com.example.carrito_service.service;

import org.springframework.stereotype.Service;

import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.repository.CarritoRepository;

//importaciones de RestTemplate para comunicacion entre servicios
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final RestTemplate restTemplate;

    @Value("${producto.service.url}")
    private String productoServiceUrl;
    
    public CarritoService(CarritoRepository carritoRepository,
                          RestTemplate restTemplate) {

        this.carritoRepository = carritoRepository;
        this.restTemplate = restTemplate;

    }

    // metodo para gaurdar carrito preguntando si la gpu existe
    public Carrito guardar(Carrito carrito) {
    String url = productoServiceUrl +
            "/gpus/" + carrito.getGpuId() + "/exists";
    Boolean existeGpu = restTemplate.getForObject(
            url,
            Boolean.class);
    if (existeGpu == null || !existeGpu) {
        throw new RuntimeException("La GPU no existe");}
    return carritoRepository.save(carrito);
    }

    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    public Carrito buscarPorId(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    public boolean existePorId(Long id) {
        return carritoRepository.existsById(id);
    }

    public Carrito actualizar(Long id, Carrito carritoActualizado) {
        Carrito carrito = carritoRepository.findById(id).orElse(null);

        if (carrito == null) {
            return null;
        }

        carrito.setUsuarioId(carritoActualizado.getUsuarioId());
        carrito.setGpuId(carritoActualizado.getGpuId());
        carrito.setCantidad(carritoActualizado.getCantidad());

        return carritoRepository.save(carrito);
    }

    public boolean eliminar(Long id) {
        if (!carritoRepository.existsById(id)) {
            return false;
        }

        carritoRepository.deleteById(id);
        return true;
    }

    public List<Carrito> buscarPorUsuarioId(Long usuarioId) {
    return carritoRepository.findByUsuarioId(usuarioId);
}

    public Integer totalProductosPorUsuario(Long usuarioId) {
    return carritoRepository.totalProductosPorUsuario(usuarioId);
}
}