package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.ClienteDTO;
import com.oracle.OSfacil.dto.response.ClienteResponseDTO;
import com.oracle.OSfacil.enums.Role;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.mapper.ClienteMapper;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Veiculo;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClienteResponseDTO criar(ClienteDTO dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RegraDeNegocioException("CPF ja cadastrado para outro cliente");
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RegraDeNegocioException("E-mail ja cadastrado para outro cliente");
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setRole(Role.ROLE_CLIENTE);
        resolverVeiculos(dto, cliente);

        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAllWithVeiculos()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO listarPorId(Long id) {
        return clienteMapper.toResponseDTO(
                clienteRepository.findByIdWithVeiculos(id)
                        .orElseThrow(() -> new RegraDeNegocioException("Cliente nao encontrado com id: " + id)));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);

        if (!cliente.getCpf().equals(dto.getCpf()) && clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RegraDeNegocioException("CPF ja cadastrado para outro cliente");
        }
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RegraDeNegocioException("E-mail ja cadastrado para outro cliente");
        }

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCpf(dto.getCpf());
        cliente.setEndereco(dto.getEndereco());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        clienteRepository.delete(buscarPorId(id));
    }

    private void resolverVeiculos(ClienteDTO dto, Cliente cliente) {
        if (dto.getVeiculoIds() == null || dto.getVeiculoIds().isEmpty()) {
            return;
        }
        Set<Veiculo> veiculos = dto.getVeiculoIds().stream()
                .map(vid -> veiculoRepository.findById(vid)
                        .orElseThrow(() -> new RegraDeNegocioException("Veiculo nao encontrado: " + vid)))
                .collect(Collectors.toSet());
        veiculos.forEach(v -> v.setCliente(cliente));
        cliente.setVeiculos(veiculos);
    }

    private Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente nao encontrado com id: " + id));
    }
}
