package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.RegisterAdminDTO;
import com.oracle.OSfacil.dto.request.RegisterDTO;
import com.oracle.OSfacil.dto.request.RegisterFuncionarioDTO;
import com.oracle.OSfacil.enums.Role;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Funcionario;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registrarCliente(RegisterDTO dto) {
        if (clienteRepository.findByEmailIgnoreCase(dto.email()).isPresent()) {
            throw new RegraDeNegocioException("E-mail ja cadastrado: " + dto.email());
        }
        if (clienteRepository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("CPF ja cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(passwordEncoder.encode(dto.password()));
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        cliente.setCpf(dto.cpf());
        cliente.setRole(Role.ROLE_CLIENTE);

        clienteRepository.save(cliente);
    }

    @Transactional
    public void registrarFuncionario(RegisterFuncionarioDTO dto) {
        validarEmailFuncionario(dto.email());
        validarCpfFuncionario(dto.cpf());

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(passwordEncoder.encode(dto.password()));
        funcionario.setCpf(dto.cpf());
        funcionario.setSalario(dto.salario());
        funcionario.setLogin(dto.login());
        funcionario.setCargo(dto.cargo());
        funcionario.setEspecialidade(dto.especialidade());
        funcionario.setTelefone(dto.telefone());
        funcionario.setRole(Role.ROLE_FUNCIONARIO);

        funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void registrarAdmin(RegisterAdminDTO dto) {
        validarEmailFuncionario(dto.email());
        validarCpfFuncionario(dto.cpf());

        Funcionario admin = new Funcionario();
        admin.setNome(dto.nome());
        admin.setEmail(dto.email());
        admin.setSenha(passwordEncoder.encode(dto.password()));
        admin.setCpf(dto.cpf());
        admin.setSalario(dto.salario());
        admin.setLogin(dto.login());
        admin.setCargo(dto.cargo());
        admin.setRole(Role.ROLE_ADMIN);

        funcionarioRepository.save(admin);
    }

    private void validarEmailFuncionario(String email) {
        if (funcionarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new RegraDeNegocioException("E-mail ja cadastrado: " + email);
        }
    }

    private void validarCpfFuncionario(String cpf) {
        if (funcionarioRepository.existsByCpf(cpf)) {
            throw new RegraDeNegocioException("CPF ja cadastrado");
        }
    }
}
