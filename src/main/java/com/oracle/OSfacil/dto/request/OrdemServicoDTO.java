package com.oracle.OSfacil.dto.request;


import com.oracle.OSfacil.enums.StatusOrdemServico;
import com.oracle.OSfacil.enums.StatusPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdemServicoDTO {
    private Long clienteId;

    private Long funcionarioId;
    @NotNull(message = "O status do serviço não pode ser vazio")
    private StatusOrdemServico statusOrdemServico;
    @NotBlank(message = "A descrição não pode ser vazio")
    private String descricao;
    @NotNull(message = "O status do pagamento não pode ser vazio ")
    private StatusPagamento statusPagamento;
    @NotNull(message = "O valor não pode ser nulo")
    @Positive
    private BigDecimal valor;

}
