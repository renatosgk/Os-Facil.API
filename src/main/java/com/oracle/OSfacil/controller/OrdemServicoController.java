package com.oracle.OSfacil.controller;

import com.oracle.OSfacil.dto.request.OrdemServicoDTO;
import com.oracle.OSfacil.dto.response.OrdemServicoResponseDTO;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.service.OrdemServicoService;
import com.oracle.OSfacil.service.PdfExportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/ordem-servicos")
@AllArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final PdfExportService pdfExportService;

    @GetMapping("/minhas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollectionModel<EntityModel<OrdemServicoResponseDTO>>> listarMinhas(
            @AuthenticationPrincipal UserDetails principal) {

        List<OrdemServicoResponseDTO> resultado;

        if (principal instanceof Cliente cliente) {
            resultado = ordemServicoService.listarPorCliente(cliente.getId());
        } else {
            resultado = ordemServicoService.listarTodos();
        }

        List<EntityModel<OrdemServicoResponseDTO>> recursos = resultado.stream()
                .map(os -> EntityModel.of(os,
                        linkTo(methodOn(OrdemServicoController.class).buscar(os.getId())).withSelfRel()))
                .toList();

        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(OrdemServicoController.class).listarMinhas(null)).withSelfRel()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<OrdemServicoResponseDTO>> criar(
            @RequestBody @Valid OrdemServicoDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal instanceof Cliente cliente) {
            dto.setClienteId(cliente.getId());
        } else if (dto.getClienteId() == null) {
            throw new RegraDeNegocioException("O Id do cliente não pode ser vazio");
        }

        OrdemServicoResponseDTO ordemNova = ordemServicoService.criar(dto);

        EntityModel<OrdemServicoResponseDTO> resource = EntityModel.of(ordemNova,
                linkTo(methodOn(OrdemServicoController.class).buscar(ordemNova.getId())).withSelfRel(),
                linkTo(methodOn(OrdemServicoController.class).atualizar(ordemNova.getId(), null, null)).withRel("atualizar"),
                linkTo(methodOn(OrdemServicoController.class).deletar(ordemNova.getId())).withRel("deletar"));

        URI location = linkTo(methodOn(OrdemServicoController.class).buscar(ordemNova.getId())).toUri();
        return ResponseEntity.created(location).body(resource);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO','ROLE_ADMIN')")
    public ResponseEntity<CollectionModel<EntityModel<OrdemServicoResponseDTO>>> listarTodos() {

        List<EntityModel<OrdemServicoResponseDTO>> ordens = ordemServicoService.listarTodos()
                .stream()
                .map(os -> EntityModel.of(os,
                        linkTo(methodOn(OrdemServicoController.class).buscar(os.getId())).withSelfRel(),
                        linkTo(methodOn(OrdemServicoController.class).atualizar(os.getId(), null, null)).withRel("atualizar"),
                        linkTo(methodOn(OrdemServicoController.class).deletar(os.getId())).withRel("deletar")))
                .toList();

        return ResponseEntity.ok(CollectionModel.of(ordens,
                linkTo(methodOn(OrdemServicoController.class).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<OrdemServicoResponseDTO>> buscar(@PathVariable Long id) {

        OrdemServicoResponseDTO os = ordemServicoService.buscar(id);

        EntityModel<OrdemServicoResponseDTO> resource = EntityModel.of(os,
                linkTo(methodOn(OrdemServicoController.class).buscar(id)).withSelfRel(),
                linkTo(methodOn(OrdemServicoController.class).atualizar(id, null, null)).withRel("atualizar"),
                linkTo(methodOn(OrdemServicoController.class).deletar(id)).withRel("deletar"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO','ROLE_ADMIN')")
    public ResponseEntity<EntityModel<OrdemServicoResponseDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdemServicoDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal instanceof Cliente cliente) {
            dto.setClienteId(cliente.getId());
        } else if (dto.getClienteId() == null) {
            throw new RegraDeNegocioException("O Id do cliente não pode ser vazio");
        }

        OrdemServicoResponseDTO atualizado = ordemServicoService.atualizar(dto, id);

        EntityModel<OrdemServicoResponseDTO> resource = EntityModel.of(atualizado,
                linkTo(methodOn(OrdemServicoController.class).buscar(id)).withSelfRel(),
                linkTo(methodOn(OrdemServicoController.class).deletar(id)).withRel("deletar"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO','ROLE_ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ordemServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) {

        byte[] pdf = pdfExportService.exportar(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ordem-servico-" + id + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
