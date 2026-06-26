package com.example.user_service.controller;

import com.example.user_service.assembler.UsuarioModelAssembler;
import com.example.user_service.dto.UsuarioDTO;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.UsuarioService;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<UsuarioDTO>> crearUsuario(@RequestBody UsuarioDTO usuarioDto) {
        Usuario nuevo = usuarioService.guardar(usuarioDto.toModel());
        EntityModel<UsuarioDTO> entityModel = assembler.toModel(UsuarioDTO.fromModel(nuevo));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioDTO>>> listarUsuarios() {
        List<EntityModel<UsuarioDTO>> usuarios = usuarioService.listar().stream()
                .map(u -> assembler.toModel(UsuarioDTO.fromModel(u)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDTO>> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(UsuarioDTO.fromModel(usuario)));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDTO>> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDto) {
        Usuario actualizado = usuarioService.actualizar(id, usuarioDto.toModel());
        return ResponseEntity.ok(assembler.toModel(UsuarioDTO.fromModel(actualizado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}