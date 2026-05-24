package com.oracle.OSfacil.controller;


import com.oracle.OSfacil.dto.request.VeiculoDTO;
import com.oracle.OSfacil.dto.response.VeiculoResponseDTO;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/veiculos")
@AllArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<EntityModel<VeiculoResponseDTO>> criar(
            @Valid @RequestBody VeiculoDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal instanceof Cliente cliente) {
            dto.setClienteId(cliente.getId());
        } else if (dto.getClienteId() == null) {
            throw new RegraDeNegocioException("O Id do cliente não pode ser vazio");
        }

        VeiculoResponseDTO novoVeiculo = veiculoService.criar(dto);

        EntityModel<VeiculoResponseDTO> resource = EntityModel.of(novoVeiculo,
                linkTo(methodOn(VeiculoController.class).listarPorId(novoVeiculo.getId())).withSelfRel(),
                linkTo(methodOn(VeiculoController.class).atualizar(novoVeiculo.getId(), null, null)).withRel("atualizar"),
                linkTo(methodOn(VeiculoController.class).deletar(novoVeiculo.getId())).withRel("deletar"),
                linkTo(methodOn(VeiculoController.class).listarTodos()).withRel("listar_todos")
        );

        URI location = linkTo(methodOn(VeiculoController.class).listarPorId(novoVeiculo.getId())).toUri();
        return ResponseEntity.created(location).body(resource);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<VeiculoResponseDTO>>> listarTodos() {

        List<EntityModel<VeiculoResponseDTO>> veiculos = veiculoService.listarTodos()
                .stream()
                .map(veiculo -> EntityModel.of(veiculo,
                        linkTo(methodOn(VeiculoController.class).listarPorId(veiculo.getId())).withSelfRel(),
                        linkTo(methodOn(VeiculoController.class).atualizar(veiculo.getId(), null, null)).withRel("atualizar"),
                        linkTo(methodOn(VeiculoController.class).deletar(veiculo.getId())).withRel("deletar")
                ))
                .toList();

        CollectionModel<EntityModel<VeiculoResponseDTO>> collection =
                CollectionModel.of(veiculos,
                        linkTo(methodOn(VeiculoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VeiculoResponseDTO>> listarPorId(@PathVariable Long id) {

        VeiculoResponseDTO veiculo = veiculoService.buscar(id);

        EntityModel<VeiculoResponseDTO> resource = EntityModel.of(veiculo,
                linkTo(methodOn(VeiculoController.class).listarPorId(id)).withSelfRel(),
                linkTo(methodOn(VeiculoController.class).atualizar(id, null, null)).withRel("atualizar"),
                linkTo(methodOn(VeiculoController.class).deletar(id)).withRel("deletar"),
                linkTo(methodOn(VeiculoController.class).listarTodos()).withRel("listar_todos")
        );

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<VeiculoResponseDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal instanceof Cliente cliente) {
            dto.setClienteId(cliente.getId());
        } else if (dto.getClienteId() == null) {
            throw new RegraDeNegocioException("O Id do cliente não pode ser vazio");
        }

        VeiculoResponseDTO atualizado = veiculoService.atualizar(id, dto);

        EntityModel<VeiculoResponseDTO> resource = EntityModel.of(atualizado,
                linkTo(methodOn(VeiculoController.class).listarPorId(id)).withSelfRel(),
                linkTo(methodOn(VeiculoController.class).deletar(id)).withRel("deletar"),
                linkTo(methodOn(VeiculoController.class).listarTodos()).withRel("listar_todos")
        );

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}