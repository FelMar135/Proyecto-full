package com.example.auth_service.service;

import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HashService hashService;

    // LOGIN
    public String login(String email, String password) {

        AuthUser user = authUserRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        String hashedInput = hashService.sha1(password);

        if (!hashedInput.equals(user.getPassword())) {
            return null;
        }

        return jwtService.generateToken(email);
    }

    // REGISTER
    public String register(AuthUser authUser) {

        AuthUser existing = authUserRepository.findByEmail(authUser.getEmail());

        if (existing != null) {
            return "Usuario ya existe!";
        }

        authUser.setPassword(hashService.sha1(authUser.getPassword()));
        authUser.setRol("USER");

        authUserRepository.save(authUser);

        return "Usuario creado exitosamente!";
    }

    // CREAR NORMAL
    public AuthUser guardar(AuthUser authUser) {

        if (authUser.getPassword() != null) {
            authUser.setPassword(hashService.sha1(authUser.getPassword()));
        }

        return authUserRepository.save(authUser);
    }

    // LISTAR
    public List<AuthUser> listar() {
        return authUserRepository.findAll();
    }

    // BUSCAR POR ID
    public AuthUser buscarPorId(Long id) {
        return authUserRepository.findById(id).orElse(null);
    }

    // EXISTE POR ID
    public boolean existePorId(Long id) {
        return authUserRepository.existsById(id);
    }

    // BUSCAR EMAIL
    public AuthUser buscarPorEmail(String email) {
        return authUserRepository.findByEmail(email);
    }

    // ACTUALIZAR
    public AuthUser actualizar(Long id, AuthUser authUserActualizado) {

        AuthUser authUser = authUserRepository.findById(id).orElse(null);

        if (authUser == null) {
            return null;
        }

        authUser.setUsername(authUserActualizado.getUsername());
        authUser.setEmail(authUserActualizado.getEmail());

        if (authUserActualizado.getPassword() != null) {
            authUser.setPassword(hashService.sha1(authUserActualizado.getPassword()));
        }

        authUser.setRol(authUserActualizado.getRol());

        return authUserRepository.save(authUser);
    }

    // ELIMINAR
    public boolean eliminar(Long id) {

        if (!authUserRepository.existsById(id)) {
            return false;
        }

        authUserRepository.deleteById(id);
        return true;
    }

    // ROLE
    public String getRole(String email) {

        AuthUser user = authUserRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        return user.getRol();
    }
}