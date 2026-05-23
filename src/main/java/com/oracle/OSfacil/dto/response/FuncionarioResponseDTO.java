package com.oracle.OSfacil.dto.response;

import com.oracle.OSfacil.enums.Role;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FuncionarioResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String login;
    private Role role;
    private BigDecimal salario;
    private String cargo;
    private String especialidade;
    private String telefone;
}
