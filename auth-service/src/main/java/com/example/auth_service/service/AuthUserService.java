package com.example.auth_service.service;

import com.example.auth_service.exception.BadRequestException;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthUserService.class);

    private final AuthUserRepository authUserRepository;
    private final JwtService jwtService;
    private final HashService hashService;

    public AuthUserService(
            AuthUserRepository authUserRepository,
            JwtService jwtService,
            HashService hashService
    ) {
        this.authUserRepository = authUserRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
    }

    public String login(String email, String password) {
        logger.info("Intentando iniciar sesión con email: {}", email);

        if (email == null || email.isBlank()) {
            logger.error("El email es obligatorio");
            throw new BadRequestException("El email es obligatorio");
        }

        if (password == null || password.isBlank()) {
            logger.error("La contraseña es obligatoria");
            throw new BadRequestException("La contraseña es obligatoria");
        }

        AuthUser user = authUserRepository.findByEmail(email);

        if (user == null) {
            logger.error("Credenciales inválidas para el email: {}", email);
            throw new BadRequestException("Credenciales inválidas");
        }

        String hashedInput = hashService.sha1(password);

        if (!hashedInput.equals(user.getPassword())) {
            logger.error("Contraseña incorrecta para el email: {}", email);
            throw new BadRequestException("Credenciales inválidas");
        }

        logger.info("Inicio de sesión exitoso para el usuario: {}", email);
        return jwtService.generateToken(email);
    }

    public String register(AuthUser authUser) {
        logger.info("Registrando usuario con email: {}", authUser.getEmail());

        validarUsuario(authUser, true);
        AuthUser existing = authUserRepository.findByEmail(authUser.getEmail());

        if (existing != null) {
            logger.error("Ya existe un usuario registrado con email: {}", authUser.getEmail());
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        authUser.setPassword(hashService.sha1(authUser.getPassword()));
        authUser.setRol("USER");

        authUserRepository.save(authUser);

        logger.info("Usuario registrado correctamente: {}", authUser.getEmail());
        return "Usuario creado exitosamente";
    }

    public AuthUser guardar(AuthUser authUser) {
        logger.info("Guardando usuario auth: {}", authUser.getEmail());

        validarUsuario(authUser, true);
        AuthUser existing = authUserRepository.findByEmail(authUser.getEmail());

        if (existing != null) {
            logger.error("Ya existe un usuario registrado con email: {}", authUser.getEmail());
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        authUser.setPassword(hashService.sha1(authUser.getPassword()));

        if (authUser.getRol() == null || authUser.getRol().isBlank()) {
            authUser.setRol("USER");
        }

        AuthUser usuarioGuardado = authUserRepository.save(authUser);

        logger.info("Usuario auth guardado correctamente con ID: {}", usuarioGuardado.getId());
        return usuarioGuardado;
    }

        public List<AuthUser> listar() {
        logger.info("Listando todos los usuarios auth");
        return authUserRepository.findAll();
    }

    public AuthUser buscarPorId(Long id) {
        logger.info("Buscando usuario auth con ID: {}", id);

        return authUserRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No existe un usuario auth con ID: {}", id);
                    return new ResourceNotFoundException("No existe un usuario auth con ID: " + id);
                });
    }

    public boolean existePorId(Long id) {
        logger.info("Verificando existencia del usuario auth con ID: {}", id);
        return authUserRepository.existsById(id);
    }

    public AuthUser buscarPorEmail(String email) {
        logger.info("Buscando usuario auth por email: {}", email);

        if (email == null || email.isBlank()) {
            logger.error("El email es obligatorio");
            throw new BadRequestException("El email es obligatorio");
        }

        AuthUser usuario = authUserRepository.findByEmail(email);

        if (usuario == null) {
            logger.error("No existe un usuario auth con email: {}", email);
            throw new ResourceNotFoundException("No existe un usuario auth con email: " + email);
        }

        return usuario;
    }

    public AuthUser actualizar(Long id, AuthUser authUserActualizado) {
        logger.info("Actualizando usuario auth con ID: {}", id);

        AuthUser authUser = authUserRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No existe un usuario auth con ID: {}", id);
                    return new ResourceNotFoundException("No existe un usuario auth con ID: " + id);
                });

        validarUsuario(authUserActualizado, false);
        AuthUser usuarioConMismoEmail = authUserRepository.findByEmail(authUserActualizado.getEmail());

        if (usuarioConMismoEmail != null && !usuarioConMismoEmail.getId().equals(id)) {
            logger.error("Ya existe otro usuario registrado con email: {}", authUserActualizado.getEmail());
            throw new BadRequestException("Ya existe otro usuario registrado con ese email");
        }

        authUser.setUsername(authUserActualizado.getUsername());
        authUser.setEmail(authUserActualizado.getEmail());

        if (authUserActualizado.getPassword() != null && !authUserActualizado.getPassword().isBlank()) {
            authUser.setPassword(hashService.sha1(authUserActualizado.getPassword()));
        }

        if (authUserActualizado.getRol() == null || authUserActualizado.getRol().isBlank()) {
            authUser.setRol("USER");
        } else {
            authUser.setRol(authUserActualizado.getRol());
        }

        logger.info("Usuario auth actualizado correctamente con ID: {}", id);
        return authUserRepository.save(authUser);
    }

    public void eliminar(Long id) {
        logger.warn("Intentando eliminar usuario auth con ID: {}", id);

        if (!authUserRepository.existsById(id)) {
            logger.error("No existe un usuario auth con ID: {}", id);
            throw new ResourceNotFoundException("No existe un usuario auth con ID: " + id);
        }

        authUserRepository.deleteById(id);

        logger.info("Usuario auth eliminado correctamente con ID: {}", id);
    }

    public String getRole(String email) {
        logger.info("Obteniendo rol del usuario con email: {}", email);

        AuthUser user = buscarPorEmail(email);
        return user.getRol();
    }

    private void validarUsuario(AuthUser authUser, boolean validarPassword) {
        if (authUser == null) {
            logger.error("El usuario auth no puede ser nulo");
            throw new BadRequestException("El usuario auth no puede ser nulo");
        }

        if (authUser.getUsername() == null || authUser.getUsername().isBlank()) {
            logger.error("El username es obligatorio");
            throw new BadRequestException("El username es obligatorio");
        }

        if (authUser.getEmail() == null || authUser.getEmail().isBlank()) {
            logger.error("El email es obligatorio");
            throw new BadRequestException("El email es obligatorio");
        }

        if (validarPassword && (authUser.getPassword() == null || authUser.getPassword().isBlank())) {
            logger.error("La contraseña es obligatoria");
            throw new BadRequestException("La contraseña es obligatoria");
        }
    }
}