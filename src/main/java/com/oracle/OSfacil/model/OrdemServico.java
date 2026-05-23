package com.oracle.OSfacil.model;


import com.oracle.OSfacil.enums.StatusOrdemServico;
import com.oracle.OSfacil.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "tb_ordemservico")
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "ordemServico_seq_generator")
    @SequenceGenerator(name = "ordemServico_seq_generator",sequenceName = "ordemServico_generator",allocationSize=1)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "cliente_id",nullable = false)
    private Cliente cliente;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdemServico statusOrdemServico;
    @Column(nullable = false)
    private String descricao;
    @Column(nullable = false,name = "statusPagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;
    @Column(nullable = false)
    private BigDecimal valor;
    @OneToMany(mappedBy = "ordemServico")
    private List<ItemProduto> itens;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDate dataCriacao;
}
