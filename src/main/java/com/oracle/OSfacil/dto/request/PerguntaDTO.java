package com.oracle.OSfacil.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerguntaDTO {

    @NotBlank(message = "A pergunta nao pode estar em branco")
    @Size(min = 5, max = 1000, message = "A pergunta deve ter entre 5 e 1000 caracteres")
    private String pergunta;
}
