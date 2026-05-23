package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.OrdemServicoDTO;
import com.oracle.OSfacil.dto.response.OrdemServicoResponseDTO;
import com.oracle.OSfacil.enums.StatusOrdemServico;
import com.oracle.OSfacil.enums.StatusPagamento;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.mapper.OrdemServicoMapper;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.OrdemServico;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.OrdemServicoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    @Transactional
    public OrdemServicoResponseDTO criar(OrdemServicoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Cliente nao encontrado com id: " + dto.getClienteId()));

        OrdemServico os = ordemServicoMapper.toEntity(dto);
        os.setCliente(cliente);
        os.setStatusOrdemServico(StatusOrdemServico.ABERTA);
        os.setStatusPagamento(StatusPagamento.PENDENTE);

        return ordemServicoMapper.toResponseDTO(ordemServicoRepository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO atualizar(OrdemServicoDTO dto, Long id) {
        OrdemServico os = buscarPorId(id);
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Cliente nao encontrado com id: " + dto.getClienteId()));

        os.setCliente(cliente);
        os.setValor(dto.getValor());
        os.setDescricao(dto.getDescricao());
        os.setStatusPagamento(dto.getStatusPagamento());
        os.setStatusOrdemServico(dto.getStatusOrdemServico());

        return ordemServicoMapper.toResponseDTO(ordemServicoRepository.save(os));
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscar(Long id) {
        return ordemServicoMapper.toResponseDTO(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarTodos() {
        return ordemServicoRepository.findAll()
                .stream()
                .map(ordemServicoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarPorCliente(Long clienteId) {
        return ordemServicoRepository.findByCliente_Id(clienteId)
                .stream()
                .map(ordemServicoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deletar(Long id) {
        ordemServicoRepository.delete(buscarPorId(id));
    }

    private OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ordem de servico nao encontrada com id: " + id));
    }
}
