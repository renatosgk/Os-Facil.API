package com.oracle.OSfacil.mapper;

import com.oracle.OSfacil.dto.request.ItemProdutoDTO;
import com.oracle.OSfacil.dto.response.ItemProdutoResponseDTO;
import com.oracle.OSfacil.model.ItemProduto;
import org.springframework.stereotype.Component;

@Component
public class ItemProdutoMapper {

    public ItemProdutoResponseDTO toResponseDTO(ItemProduto item) {
        ItemProdutoResponseDTO dto = new ItemProdutoResponseDTO();
        dto.setId(item.getId());
        dto.setQuantidade(item.getQuantidade());
        dto.setValorUnitario(item.getValorUnitario());
        dto.setSubtotal(item.getSubtotal());

        if (item.getProduto() != null) {
            dto.setProdutoId(item.getProduto().getId());
            dto.setNomeProduto(item.getProduto().getNome());
        }
        if (item.getOrdemServico() != null) {
            dto.setOrdemServicoId(item.getOrdemServico().getId());
        }

        return dto;
    }

    public ItemProduto toEntity(ItemProdutoDTO dto) {
        ItemProduto item = new ItemProduto();
        item.setQuantidade(dto.getQuantidade());
        item.setValorUnitario(dto.getValorUnitario());
        return item;
    }
}
