package com.oracle.OSfacil.controller;

import com.oracle.OSfacil.dto.request.PerguntaDTO;
import com.oracle.OSfacil.dto.response.RespostaDTO;
import com.oracle.OSfacil.service.IAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistente")
@RequiredArgsConstructor
public class AssistenteMecanicaController {

    private final IAiService aiService;

    @PostMapping("/mecanica")
    public ResponseEntity<RespostaDTO> perguntar(@RequestBody @Valid PerguntaDTO dto) {
        return ResponseEntity.ok(new RespostaDTO(aiService.responder(dto.getPergunta())));
    }
}
