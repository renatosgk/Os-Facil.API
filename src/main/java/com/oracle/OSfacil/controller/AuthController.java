package com.oracle.OSfacil.controller;

import com.oracle.OSfacil.dto.request.RegisterFuncionarioDTO;
import com.oracle.OSfacil.model.autenticacao.DadosLogin;
import com.oracle.OSfacil.dto.request.RegisterDTO;
import com.oracle.OSfacil.dto.response.TokenResponseDTO;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Funcionario;
import com.oracle.OSfacil.enums.Role;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.FuncionarioRepository;
import com.oracle.OSfacil.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final FuncionarioRepository funcionarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          ClienteRepository clienteRepository,
                          PasswordEncoder passwordEncoder,
                          FuncionarioRepository funcionarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.funcionarioRepository = funcionarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@Valid @RequestBody DadosLogin dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
        var authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails usuarioLogado = (UserDetails) authentication.getPrincipal();

        String tokenAcesso = tokenService.gerarToken(usuarioLogado);

        TokenResponseDTO response = new TokenResponseDTO();
        response.setTokenAcesso(tokenAcesso);

        if (usuarioLogado instanceof Cliente cliente) {
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
        } else if (usuarioLogado instanceof Funcionario func) {
            response.setNome(func.getNome());
            response.setEmail(func.getEmail());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
        if (clienteRepository.findByEmailIgnoreCase(registerDTO.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Já possui um cliente cadastrado com esse e-mail");
        }

        String senhaCriptografada = passwordEncoder.encode(registerDTO.password());

        Cliente cliente = new Cliente();
        cliente.setNome(registerDTO.nome());
        cliente.setEmail(registerDTO.email());
        cliente.setSenha(senhaCriptografada);
        cliente.setTelefone(registerDTO.telefone());
        cliente.setEndereco(registerDTO.endereco());
        cliente.setCpf(registerDTO.cpf());
        cliente.setRole(Role.ROLE_CLIENTE);

        clienteRepository.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body("Cliente cadastrado com sucesso");
    }

    @PostMapping("/register-funcionario")
    @Transactional
    public ResponseEntity<?> registerFuncionario(@RequestBody @Valid RegisterFuncionarioDTO dto) {

        if (funcionarioRepository.findByEmailIgnoreCase(dto.email()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Já existe um funcionário com esse e-mail");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.password());

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(senhaCriptografada);
        funcionario.setCpf(dto.cpf());
        funcionario.setSalario(dto.salario());
        funcionario.setLogin(dto.login());
        funcionario.setRole(Role.ROLE_FUNCIONARIO);

        funcionarioRepository.save(funcionario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Funcionário cadastrado com sucesso");
    }
}