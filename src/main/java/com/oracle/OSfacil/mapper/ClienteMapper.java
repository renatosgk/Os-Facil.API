package com.oracle.OSfacil.mapper;

import com.oracle.OSfacil.dto.request.ClienteDTO;
import com.oracle.OSfacil.dto.response.ClienteResponseDTO;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Veiculo;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setCpf(cliente.getCpf());
        dto.setEndereco(cliente.getEndereco());

        if (cliente.getVeiculos() != null && !cliente.getVeiculos().isEmpty()) {
            Set<Long> ids = cliente.getVeiculos().stream()
                    .map(Veiculo::getId)
                    .collect(Collectors.toSet());
            dto.setVeiculosIds(ids);
        }

        return dto;
    }

    public Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCpf(dto.getCpf());
        cliente.setSenha(dto.getSenha());
        cliente.setEndereco(dto.getEndereco());
        return cliente;
    }
}
