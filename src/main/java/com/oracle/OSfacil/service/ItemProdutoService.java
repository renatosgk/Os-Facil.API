package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.ItemProdutoDTO;
import com.oracle.OSfacil.dto.response.ItemProdutoResponseDTO;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.mapper.ItemProdutoMapper;
import com.oracle.OSfacil.model.ItemProduto;
import com.oracle.OSfacil.model.OrdemServico;
import com.oracle.OSfacil.model.Produto;
import com.oracle.OSfacil.repository.ItemProdutoRepository;
import com.oracle.OSfacil.repository.OrdemServicoRepository;
import com.oracle.OSfacil.repository.ProdutoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemProdutoService {

    private final ItemProdutoRepository itemProdutoRepository;
    private final ProdutoRepository produtoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemProdutoMapper itemProdutoMapper;

    @Transactional
    public ItemProdutoResponseDTO criar(ItemProdutoDTO dto) {
        Produto produto = buscarProduto(dto.getProdutoId());
        OrdemServico os = buscarOrdemServico(dto.getOrdemServicoId());

        ItemProduto item = itemProdutoMapper.toEntity(dto);
        item.setProduto(produto);
        item.setOrdemServico(os);
        item.setSubtotal(dto.getValorUnitario().multiply(BigDecimal.valueOf(dto.getQuantidade())));

        return itemProdutoMapper.toResponseDTO(itemProdutoRepository.save(item));
    }

    @Transactional(readOnly = true)
    public ItemProdutoResponseDTO buscar(Long id) {
        return itemProdutoMapper.toResponseDTO(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<ItemProdutoResponseDTO> listarTodos() {
        return itemProdutoRepository.findAll()
                .stream()
                .map(itemProdutoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ItemProdutoResponseDTO atualizar(Long id, ItemProdutoDTO dto) {
        ItemProduto item = buscarPorId(id);
        item.setProduto(buscarProduto(dto.getProdutoId()));
        item.setOrdemServico(buscarOrdemServico(dto.getOrdemServicoId()));
        item.setQuantidade(dto.getQuantidade());
        item.setValorUnitario(dto.getValorUnitario());
        item.setSubtotal(dto.getValorUnitario().multiply(BigDecimal.valueOf(dto.getQuantidade())));

        return itemProdutoMapper.toResponseDTO(itemProdutoRepository.save(item));
    }

    @Transactional
    public void deletar(Long id) {
        itemProdutoRepository.delete(buscarPorId(id));
    }

    private ItemProduto buscarPorId(Long id) {
        return itemProdutoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Item nao encontrado com id: " + id));
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Produto nao encontrado com id: " + id));
    }

    private OrdemServico buscarOrdemServico(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ordem de servico nao encontrada com id: " + id));
    }
}
