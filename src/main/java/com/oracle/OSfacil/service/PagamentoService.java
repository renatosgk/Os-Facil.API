package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.PagamentoDTO;
import com.oracle.OSfacil.dto.response.PagamentoResponseDTO;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.mapper.PagamentoMapper;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Pagamento;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.PagamentoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional
    public PagamentoResponseDTO criar(PagamentoDTO dto, Cliente logado) {
        Pagamento pagamento = pagamentoMapper.toEntity(dto);
        pagamento.setCliente(logado);
        return pagamentoMapper.toResponseDTO(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponseDTO atualizar(Long id, PagamentoDTO dto, Cliente logado) {
        Pagamento pagamento = buscarPorId(id);

        if (!pagamento.getCliente().getId().equals(logado.getId())) {
            throw new RegraDeNegocioException("Sem permissao para alterar este pagamento");
        }

        pagamento.setFormaPagamento(dto.getFormaPagamento());
        pagamento.setValor(dto.getValor());
        return pagamentoMapper.toResponseDTO(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public void deletar(Long id) {
        pagamentoRepository.deleteById(buscarPorId(id).getId());
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarTodos() {
        return pagamentoRepository.findAll()
                .stream()
                .map(pagamentoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscar(Long id) {
        return pagamentoMapper.toResponseDTO(buscarPorId(id));
    }

    private Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Pagamento nao encontrado com id: " + id));
    }
}
