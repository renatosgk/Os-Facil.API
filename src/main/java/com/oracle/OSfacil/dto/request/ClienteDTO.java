package com.oracle.OSfacil.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashSet;
import java.util.Set;

@Data
public class ClienteDTO extends RepresentationModel<ClienteDTO> {
    @NotBlank(message = "O nome não pode estar vazio")
    private String nome;
    @NotBlank(message = "O cpf não pode estar vazio")
    @CPF(message = "cpf inválido")
    private String cpf;
    @NotBlank(message = "O email não pode estar vazio")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email deve estar em um formato válido"
    )
    private String email;
    @NotBlank(message = "senhao nao pode estar vazio")
    private String senha;
    private Set<Long> veiculoIds = new HashSet<>();
    @NotBlank(message = "O telefone não pode estar vazio")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    private String telefone;
    @NotBlank(message = "O endereço não pode estar vazio")
    private String endereco;
}
