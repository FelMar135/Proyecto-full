package com.example.auth_service.controller;

import com.example.auth_service.assembler.AuthUserModelAssembler;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.service.AuthUserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/auth")
public class AuthUserController {

    private final AuthUserService authUserService;
    private final AuthUserModelAssembler authUserModelAssembler;

    public AuthUserController(
            AuthUserService authUserService,
            AuthUserModelAssembler authUserModelAssembler
    ) {
        this.authUserService = authUserService;
        this.authUserModelAssembler = authUserModelAssembler;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthUser authUser) {
        String token = authUserService.login(
                authUser.getEmail(),
                authUser.getPassword()
        );

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthUser authUser) {
        String mensaje = authUserService.register(authUser);
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping
    public ResponseEntity<EntityModel<AuthUser>> crearUsuario(@RequestBody AuthUser authUser) {
        AuthUser nuevoUsuario = authUserService.guardar(authUser);
        return ResponseEntity.ok(authUserModelAssembler.toModel(nuevoUsuario));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AuthUser>>> listarUsuarios() {
        List<EntityModel<AuthUser>> usuarios = authUserService.listar()
                .stream()
                .map(authUserModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<AuthUser>> respuesta = CollectionModel.of(
                usuarios,
                linkTo(methodOn(AuthUserController.class).listarUsuarios()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AuthUser>> buscarUsuarioPorId(@PathVariable Long id) {
        AuthUser usuario = authUserService.buscarPorId(id);
        return ResponseEntity.ok(authUserModelAssembler.toModel(usuario));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(authUserService.existePorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<AuthUser>> buscarUsuarioPorEmail(@PathVariable String email) {
        AuthUser usuario = authUserService.buscarPorEmail(email);
        return ResponseEntity.ok(authUserModelAssembler.toModel(usuario));
    }

    @GetMapping("/role/{email}")
    public ResponseEntity<String> obtenerRolPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(authUserService.getRole(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AuthUser>> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody AuthUser authUser
    ) {
        AuthUser actualizado = authUserService.actualizar(id, authUser);
        return ResponseEntity.ok(authUserModelAssembler.toModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        authUserService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}