package com.oracle.OSfacil.service;

import com.oracle.OSfacil.enums.FormaPagamento;
import com.oracle.OSfacil.enums.StatusOrdemServico;
import com.oracle.OSfacil.enums.StatusPagamento;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.model.*;
import com.oracle.OSfacil.repository.OrdemServicoRepository;
import com.oracle.OSfacil.repository.PagamentoRepository;
import com.oracle.OSfacil.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoPdfServiceTest {

    @Mock OrdemServicoRepository ordemServicoRepository;
    @Mock PagamentoRepository pagamentoRepository;
    @Mock VeiculoRepository veiculoRepository;

    @InjectMocks OrdemServicoPdfService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Joao Silva");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A, 1");
        cliente.setCpf("12345678900");
        cliente.setSenha("senha");
    }

    // -------------------------------------------------------------------------
    // Testes de erro
    // -------------------------------------------------------------------------

    @Test
    void exportar_quandoOsNaoExiste_lancaRegraDeNegocioException() {
        when(ordemServicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.exportar(99L))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // Testes de sucesso – formato do PDF
    // -------------------------------------------------------------------------

    @Test
    void exportar_quandoOsValida_retornaByteArrayComAssinaturaDosPdf() {
        OrdemServico os = buildOs(Collections.emptyList());
        configurarMocks(os, Collections.emptyList(), Collections.emptyList());

        byte[] resultado = service.exportar(1L);

        assertThat(resultado).isNotNull().isNotEmpty();
        // Assinatura magica do PDF: %PDF
        assertThat(new String(resultado, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportar_quandoOsSemItens_gerarPdfSemLancarExcecao() {
        OrdemServico os = buildOs(Collections.emptyList());
        configurarMocks(os, Collections.emptyList(), Collections.emptyList());

        assertThatCode(() -> service.exportar(1L)).doesNotThrowAnyException();
    }

    @Test
    void exportar_quandoOsComItens_gerarPdfSemLancarExcecao() {
        Produto produto = new Produto();
        produto.setId(10L);
        produto.setNome("Oleo Motor 5W30");
        produto.setPreco(new BigDecimal("45.00"));
        produto.setQuantidade(10);

        OrdemServico os = buildOs(Collections.emptyList());

        ItemProduto item = new ItemProduto();
        item.setId(100L);
        item.setProduto(produto);
        item.setOrdemServico(os);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("45.00"));
        item.setSubtotal(new BigDecimal("90.00"));

        os.setItens(List.of(item));
        configurarMocks(os, Collections.emptyList(), Collections.emptyList());

        byte[] resultado = service.exportar(1L);

        assertThat(resultado).isNotEmpty();
        assertThat(new String(resultado, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportar_quandoOsComPagamento_gerarPdfSemLancarExcecao() {
        OrdemServico os = buildOs(Collections.emptyList());

        Pagamento pagamento = new Pagamento();
        pagamento.setId(50L);
        pagamento.setFormaPagamento(FormaPagamento.PIX);
        pagamento.setValor(new BigDecimal("200.00"));
        pagamento.setCliente(cliente);

        configurarMocks(os, List.of(pagamento), Collections.emptyList());

        assertThatCode(() -> service.exportar(1L)).doesNotThrowAnyException();
    }

    @Test
    void exportar_quandoOsComVeiculos_gerarPdfSemLancarExcecao() {
        OrdemServico os = buildOs(Collections.emptyList());

        Veiculo veiculo = new Veiculo();
        veiculo.setId(20L);
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2022);
        veiculo.setPlaca("ABC1D23");
        veiculo.setCor("Branco");
        veiculo.setCliente(cliente);

        configurarMocks(os, Collections.emptyList(), List.of(veiculo));

        assertThatCode(() -> service.exportar(1L)).doesNotThrowAnyException();
    }

    @Test
    void exportar_quandoDataCriacaoNula_usaNaNoHeader() {
        OrdemServico os = buildOs(Collections.emptyList());
        os.setDataCriacao(null);
        configurarMocks(os, Collections.emptyList(), Collections.emptyList());

        assertThatCode(() -> service.exportar(1L)).doesNotThrowAnyException();
    }

    @Test
    void exportar_quandoMultiplasPaginas_gerarPdfSemLancarExcecao() {
        OrdemServico os = buildOs(Collections.emptyList());

        List<ItemProduto> itens = criarItensFicticio(os, 40);
        os.setItens(itens);
        configurarMocks(os, Collections.emptyList(), Collections.emptyList());

        byte[] resultado = service.exportar(1L);

        assertThat(resultado).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private OrdemServico buildOs(List<ItemProduto> itens) {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setCliente(cliente);
        os.setDescricao("Revisao completa do veiculo");
        os.setValor(new BigDecimal("350.00"));
        os.setStatusOrdemServico(StatusOrdemServico.EM_ANDAMENTO);
        os.setStatusPagamento(StatusPagamento.PENDENTE);
        os.setDataCriacao(LocalDate.of(2026, 5, 22));
        os.setItens(itens);
        return os;
    }

    private List<ItemProduto> criarItensFicticio(OrdemServico os, int quantidade) {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Peca Generica");
        produto.setPreco(BigDecimal.TEN);
        produto.setQuantidade(100);

        return java.util.stream.IntStream.range(0, quantidade)
                .mapToObj(i -> {
                    ItemProduto item = new ItemProduto();
                    item.setId((long) i);
                    item.setProduto(produto);
                    item.setOrdemServico(os);
                    item.setQuantidade(1);
                    item.setValorUnitario(BigDecimal.TEN);
                    item.setSubtotal(BigDecimal.TEN);
                    return item;
                })
                .toList();
    }

    private void configurarMocks(OrdemServico os, List<Pagamento> pagamentos, List<Veiculo> veiculos) {
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(os));
        when(pagamentoRepository.findByClienteId(1L)).thenReturn(pagamentos);
        when(veiculoRepository.findByClienteId(1L)).thenReturn(veiculos);
    }
}
