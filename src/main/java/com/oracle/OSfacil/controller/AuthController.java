package com.oracle.OSfacil.controller;

import com.oracle.OSfacil.dto.request.RegisterAdminDTO;
import com.oracle.OSfacil.dto.request.RegisterFuncionarioDTO;
import com.oracle.OSfacil.model.autenticacao.DadosLogin;
import com.oracle.OSfacil.dto.request.RegisterDTO;
import com.oracle.OSfacil.dto.response.TokenResponseDTO;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Funcionario;
import com.oracle.OSfacil.service.RegistroService;
import com.oracle.OSfacil.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RegistroService registroService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody DadosLogin dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
        var authentication = authenticationManager.authenticate(authToken);

        UserDetails usuarioLogado = (UserDetails) authentication.getPrincipal();
        TokenResponseDTO response = new TokenResponseDTO();
        response.setTokenAcesso(tokenService.gerarToken(usuarioLogado));

        if (usuarioLogado instanceof Cliente c) {
            response.setNome(c.getNome());
            response.setEmail(c.getEmail());
            response.setRole(c.getRole().name());
        } else if (usuarioLogado instanceof Funcionario f) {
            response.setNome(f.getNome());
            response.setEmail(f.getEmail());
            response.setRole(f.getRole().name());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO dto) {
        registroService.registrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cliente cadastrado com sucesso");
    }

    @PostMapping("/register-funcionario")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> registerFuncionario(@RequestBody @Valid RegisterFuncionarioDTO dto) {
        registroService.registrarFuncionario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Funcionario cadastrado com sucesso");
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> registerAdmin(@RequestBody @Valid RegisterAdminDTO dto) {
        registroService.registrarAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Administrador cadastrado com sucesso");
    }
}
