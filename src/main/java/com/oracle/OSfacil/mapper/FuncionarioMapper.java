package com.oracle.OSfacil.mapper;


import com.oracle.OSfacil.dto.request.FuncionarioDTO;
import com.oracle.OSfacil.dto.response.FuncionarioResponseDTO;
import com.oracle.OSfacil.model.Funcionario;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class FuncionarioMapper {

    public FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
        FuncionarioResponseDTO dto = new FuncionarioResponseDTO();
        dto.setId(funcionario.getId());
        dto.setNome(funcionario.getNome());
        dto.setCpf(funcionario.getCpf());
        dto.setEmail(funcionario.getEmail());
        dto.setRole(funcionario.getRole());
        dto.setLogin(funcionario.getLogin());
        dto.setSalario(funcionario.getSalario());
        dto.setCargo(funcionario.getCargo());
        dto.setEspecialidade(funcionario.getEspecialidade());
        dto.setTelefone(funcionario.getTelefone());
        return dto;
    }

    public Funcionario toEntity(FuncionarioDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setCpf(dto.getCpf());
        funcionario.setEmail(dto.getEmail());
        funcionario.setRole(dto.getRole());
        funcionario.setLogin(dto.getLogin());
        funcionario.setSenha(dto.getSenha());
        funcionario.setSalario(dto.getSalario());
        return funcionario;
    }
}
