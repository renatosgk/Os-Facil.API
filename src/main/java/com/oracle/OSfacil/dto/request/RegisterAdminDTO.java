package com.oracle.OSfacil.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterAdminDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String cpf,
        @NotBlank String login,
        @NotNull BigDecimal salario,
        String cargo
) {}
