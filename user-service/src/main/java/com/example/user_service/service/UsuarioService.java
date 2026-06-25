package com.example.user_service.service;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.Usuario;
import com.example.user_service.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j // log
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
        log.info("Iniciando guardado de nuevo usuario con email: {}", usuario.getEmail());
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario guardado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioGuardado;
    }

    public List<Usuario> listar() {
        log.info("Obteniendo la lista de todos los usuarios registrados.");
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                // Lanzamos excepción en vez de retornar null
                .orElseThrow(() -> {
                    log.error("Fallo al buscar: Usuario no encontrado con ID {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
                });
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        log.info("Intentando actualizar usuario con ID: {}", id);
        
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo al actualizar: Usuario no encontrado con ID {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
                });

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());

        Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario actualizado correctamente con ID: {}", id);
        
        return usuarioGuardado;
    }

    public void eliminar(Long id) {
        log.warn("Solicitud para eliminar usuario con ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            log.error("Fallo al eliminar: Usuario no encontrado con ID {}", id);
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado correctamente. ID: {}", id);
    }
}