package com.example.auth_service.controller;

import com.example.auth_service.model.AuthUser;
import com.example.auth_service.service.AuthUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthUserController {

    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthUser authUser) {

        String resultado = authUserService.register(authUser);

        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);

        return ResponseEntity.ok(resp);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        String token = authUserService.login(email, password);

        Map<String, String> resp = new HashMap<>();

        if (token == null) {
            resp.put("status", "error");
            resp.put("token", "");
            return ResponseEntity.status(401).body(resp);
        }

        resp.put("status", "ok");
        resp.put("token", token);

        return ResponseEntity.ok(resp);
    }

    // CREAR NORMAL
    @PostMapping
    public ResponseEntity<AuthUser> crearUsuarioAuth(@RequestBody AuthUser authUser) {
        AuthUser nuevo = authUserService.guardar(authUser);
        return ResponseEntity.ok(nuevo);
    }

    // LISTAR
    @GetMapping
    public ResponseEntity<List<AuthUser>> listarUsuarios() {
        return ResponseEntity.ok(authUserService.listar());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<AuthUser> buscarPorId(@PathVariable Long id) {

        AuthUser usuario = authUserService.buscarPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    // EXISTE POR ID
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        return ResponseEntity.ok(authUserService.existePorId(id));
    }

    // BUSCAR POR EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<AuthUser> buscarPorEmail(@PathVariable String email) {

        AuthUser usuario = authUserService.buscarPorEmail(email);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<AuthUser> actualizar(@PathVariable Long id,
                                               @RequestBody AuthUser authUser) {

        AuthUser actualizado = authUserService.actualizar(id, authUser);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizado);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        boolean eliminado = authUserService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}