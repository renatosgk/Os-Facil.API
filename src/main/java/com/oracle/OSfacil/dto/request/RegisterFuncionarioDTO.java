package com.oracle.OSfacil.dto.request;

import java.math.BigDecimal;

public record RegisterFuncionarioDTO(
        String email,
        String password,
        String nome,
        String cpf,
        BigDecimal salario,
        String login
) {}

