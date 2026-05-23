package com.oracle.OSfacil.service;

import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.model.ItemProduto;
import com.oracle.OSfacil.model.OrdemServico;
import com.oracle.OSfacil.model.Pagamento;
import com.oracle.OSfacil.model.Veiculo;
import com.oracle.OSfacil.repository.OrdemServicoRepository;
import com.oracle.OSfacil.repository.PagamentoRepository;
import com.oracle.OSfacil.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdemServicoPdfService implements PdfExportService {

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LINE_H = 16f;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final OrdemServicoRepository ordemServicoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportar(Long ordemServicoId) {
        OrdemServico os = ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ordem de servico nao encontrada com id: " + ordemServicoId));

        List<Pagamento> pagamentos = pagamentoRepository.findByClienteId(os.getCliente().getId());
        List<Veiculo> veiculos = veiculoRepository.findByClienteId(os.getCliente().getId());

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            Escritor w = new Escritor(doc);
            renderCabecalho(w, os, veiculos, bold, regular);
            renderTabela(w, os.getItens(), bold, regular);
            renderResumo(w, os, pagamentos, bold, regular);
            w.fechar();

            doc.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RegraDeNegocioException("Erro ao gerar PDF da OS " + ordemServicoId + ": " + e.getMessage());
        }
    }

    private void renderCabecalho(Escritor w, OrdemServico os, List<Veiculo> veiculos,
                                  PDFont bold, PDFont regular) throws IOException {
        w.texto(bold, 18, MARGIN, "OS FACIL - Ordem de Servico #" + os.getId());
        w.pularLinha(10);
        w.linha();
        w.pularLinha(12);

        String data = os.getDataCriacao() != null
                ? os.getDataCriacao().format(DATE_FMT)
                : "N/A";

        w.par(bold, regular, 11, "Data:", data);
        w.par(bold, regular, 11, "Cliente:", sanitizar(os.getCliente().getNome()));
        w.par(bold, regular, 11, "Email:", sanitizar(os.getCliente().getEmail()));
        w.par(bold, regular, 11, "Telefone:", sanitizar(os.getCliente().getTelefone()));
        w.par(bold, regular, 11, "Status OS:", os.getStatusOrdemServico().name());
        w.pularLinha(6);

        w.texto(bold, 11, MARGIN, "Veiculos:");
        w.pularLinha(LINE_H);

        if (veiculos.isEmpty()) {
            w.texto(regular, 10, MARGIN + 10, "Nenhum veiculo cadastrado");
            w.pularLinha(LINE_H);
        } else {
            for (Veiculo v : veiculos) {
                String linha = v.getMarca() + " " + v.getModelo()
                        + " (" + v.getAno() + ") - Placa: " + v.getPlaca();
                w.texto(regular, 10, MARGIN + 10, sanitizar(linha));
                w.pularLinha(LINE_H);
            }
        }
    }

    private void renderTabela(Escritor w, List<ItemProduto> itens,
                               PDFont bold, PDFont regular) throws IOException {
        w.pularLinha(8);
        w.linha();
        w.pularLinha(12);
        w.texto(bold, 13, MARGIN, "ITENS DE MANUTENCAO");
        w.pularLinha(20);

        float[] cX = {MARGIN, MARGIN + 200, MARGIN + 280, MARGIN + 385};
        w.texto(bold, 10, cX[0], "Produto / Servico");
        w.textoAbs(bold, 10, cX[1], w.y, "Qtd");
        w.textoAbs(bold, 10, cX[2], w.y, "Vlr. Unit.");
        w.textoAbs(bold, 10, cX[3], w.y, "Subtotal");
        w.pularLinha(6);
        w.linha();
        w.pularLinha(13);

        if (itens == null || itens.isEmpty()) {
            w.texto(regular, 10, MARGIN, "Nenhum item cadastrado nesta OS");
            w.pularLinha(LINE_H);
            return;
        }

        for (ItemProduto item : itens) {
            w.verificarEspaco(LINE_H + 5);
            String nome = item.getProduto() != null
                    ? sanitizar(item.getProduto().getNome())
                    : "N/A";
            if (nome.length() > 28) nome = nome.substring(0, 25) + "...";

            w.texto(regular, 10, cX[0], nome);
            w.textoAbs(regular, 10, cX[1], w.y, String.valueOf(item.getQuantidade()));
            w.textoAbs(regular, 10, cX[2], w.y, moeda(item.getValorUnitario()));
            w.textoAbs(regular, 10, cX[3], w.y, moeda(item.getSubtotal()));
            w.pularLinha(LINE_H);
        }
    }

    private void renderResumo(Escritor w, OrdemServico os, List<Pagamento> pagamentos,
                               PDFont bold, PDFont regular) throws IOException {
        w.pularLinha(8);
        w.linha();
        w.pularLinha(12);
        w.texto(bold, 13, MARGIN, "RESUMO FINANCEIRO");
        w.pularLinha(20);

        w.par(bold, regular, 11, "Valor Total:", moeda(os.getValor()));
        w.par(bold, regular, 11, "Status do Pagamento:", os.getStatusPagamento().name());

        String formas = pagamentos.isEmpty()
                ? "Nao registrado"
                : pagamentos.stream()
                        .map(p -> p.getFormaPagamento().name())
                        .distinct()
                        .collect(Collectors.joining(", "));
        w.par(bold, regular, 11, "Forma de Pagamento:", formas);
        w.par(bold, regular, 11, "Descricao:", sanitizar(os.getDescricao()));
    }

    private String moeda(BigDecimal valor) {
        return valor != null ? CURRENCY.format(valor) : "R$ 0,00";
    }

    private String sanitizar(String texto) {
        if (texto == null) return "";
        return texto.chars()
                .filter(c -> c >= 0x20 && c <= 0xFF)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static final class Escritor {

        private final PDDocument doc;
        private PDPageContentStream cs;
        float y;

        Escritor(PDDocument doc) throws IOException {
            this.doc = doc;
            abrirPagina();
        }

        void texto(PDFont font, float size, float x, String text) throws IOException {
            cs.beginText();
            cs.setFont(font, size);
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
        }

        void textoAbs(PDFont font, float size, float x, float absY, String text) throws IOException {
            cs.beginText();
            cs.setFont(font, size);
            cs.newLineAtOffset(x, absY);
            cs.showText(text);
            cs.endText();
        }

        void par(PDFont bold, PDFont regular, float size, String label, String valor) throws IOException {
            verificarEspaco(LINE_H + 4);
            texto(bold, size, MARGIN, label);
            textoAbs(regular, size, MARGIN + 160, y, valor);
            pularLinha(LINE_H);
        }

        void linha() throws IOException {
            cs.moveTo(MARGIN, y);
            cs.lineTo(PAGE_WIDTH - MARGIN, y);
            cs.stroke();
        }

        void pularLinha(float delta) throws IOException {
            y -= delta;
            verificarEspaco(0);
        }

        void verificarEspaco(float necessario) throws IOException {
            if (y < MARGIN + necessario) {
                cs.close();
                abrirPagina();
            }
        }

        void fechar() throws IOException {
            cs.close();
        }

        private void abrirPagina() throws IOException {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = PAGE_HEIGHT - MARGIN;
        }
    }
}
