package com.example.auth_service.service;

import com.example.auth_service.exception.BadRequestException;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserService {

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
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }

        AuthUser user = authUserRepository.findByEmail(email);

        if (user == null) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String hashedInput = hashService.sha1(password);

        if (!hashedInput.equals(user.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        return jwtService.generateToken(email);
    }

    public String register(AuthUser authUser) {
        validarUsuario(authUser, true);

        AuthUser existing = authUserRepository.findByEmail(authUser.getEmail());

        if (existing != null) {
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        authUser.setPassword(hashService.sha1(authUser.getPassword()));
        authUser.setRol("USER");

        authUserRepository.save(authUser);

        return "Usuario creado exitosamente";
    }

    public AuthUser guardar(AuthUser authUser) {
        validarUsuario(authUser, true);

        AuthUser existing = authUserRepository.findByEmail(authUser.getEmail());

        if (existing != null) {
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        authUser.setPassword(hashService.sha1(authUser.getPassword()));

        if (authUser.getRol() == null || authUser.getRol().isBlank()) {
            authUser.setRol("USER");
        }

        return authUserRepository.save(authUser);
    }

    public List<AuthUser> listar() {
        return authUserRepository.findAll();
    }

    public AuthUser buscarPorId(Long id) {
        return authUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario auth con ID: " + id));
    }

    public boolean existePorId(Long id) {
        return authUserRepository.existsById(id);
    }

    public AuthUser buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        AuthUser usuario = authUserRepository.findByEmail(email);

        if (usuario == null) {
            throw new ResourceNotFoundException("No existe un usuario auth con email: " + email);
        }

        return usuario;
    }

    public AuthUser actualizar(Long id, AuthUser authUserActualizado) {
        AuthUser authUser = authUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario auth con ID: " + id));

        validarUsuario(authUserActualizado, false);

        AuthUser usuarioConMismoEmail = authUserRepository.findByEmail(authUserActualizado.getEmail());

        if (usuarioConMismoEmail != null && !usuarioConMismoEmail.getId().equals(id)) {
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

        return authUserRepository.save(authUser);
    }

    public void eliminar(Long id) {
        if (!authUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un usuario auth con ID: " + id);
        }

        authUserRepository.deleteById(id);
    }

    public String getRole(String email) {
        AuthUser user = buscarPorEmail(email);
        return user.getRol();
    }

    private void validarUsuario(AuthUser authUser, boolean validarPassword) {
        if (authUser == null) {
            throw new BadRequestException("El usuario auth no puede ser nulo");
        }

        if (authUser.getUsername() == null || authUser.getUsername().isBlank()) {
            throw new BadRequestException("El username es obligatorio");
        }

        if (authUser.getEmail() == null || authUser.getEmail().isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        if (validarPassword && (authUser.getPassword() == null || authUser.getPassword().isBlank())) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
    }
}