package com.example.auth_service.controller;

import com.example.auth_service.assembler.AuthUserModelAssembler;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Operaciones relacionadas con autenticación y gestión de usuarios")
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
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<String> login(@RequestBody AuthUser authUser) {
        String token = authUserService.login(
                authUser.getEmail(),
                authUser.getPassword()
        );

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<String> register(@RequestBody AuthUser authUser) {
        String mensaje = authUserService.register(authUser);
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping
    @Operation(summary = "Crear un usuario")
    public ResponseEntity<EntityModel<AuthUser>> crearUsuario(@RequestBody AuthUser authUser) {
        AuthUser nuevoUsuario = authUserService.guardar(authUser);
        return ResponseEntity.ok(authUserModelAssembler.toModel(nuevoUsuario));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
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
    @Operation(summary = "Buscar usuario por ID")
    public ResponseEntity<EntityModel<AuthUser>> buscarUsuarioPorId(@PathVariable Long id) {
        AuthUser usuario = authUserService.buscarPorId(id);
        return ResponseEntity.ok(authUserModelAssembler.toModel(usuario));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verificar existencia de un usuario")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(authUserService.existePorId(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email")
    public ResponseEntity<EntityModel<AuthUser>> buscarUsuarioPorEmail(@PathVariable String email) {
        AuthUser usuario = authUserService.buscarPorEmail(email);
        return ResponseEntity.ok(authUserModelAssembler.toModel(usuario));
    }

    @GetMapping("/role/{email}")
    @Operation(summary = "Obtener rol de un usuario por email")
    public ResponseEntity<String> obtenerRolPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(authUserService.getRole(email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario")
    public ResponseEntity<EntityModel<AuthUser>> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody AuthUser authUser
    ) {
        AuthUser actualizado = authUserService.actualizar(id, authUser);
        return ResponseEntity.ok(authUserModelAssembler.toModel(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        authUserService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}