package com.oracle.OSfacil.service;

import com.oracle.OSfacil.dto.request.VeiculoDTO;
import com.oracle.OSfacil.dto.response.VeiculoResponseDTO;
import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.mapper.VeiculoMapper;
import com.oracle.OSfacil.model.Cliente;
import com.oracle.OSfacil.model.Veiculo;
import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.VeiculoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoMapper veiculoMapper;

    @Transactional
    public VeiculoResponseDTO criar(VeiculoDTO dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RegraDeNegocioException("Placa ja cadastrada: " + dto.getPlaca());
        }
        Cliente cliente = buscarCliente(dto.getClienteId());
        Veiculo veiculo = veiculoMapper.toEntity(dto);
        veiculo.setCliente(cliente);
        return veiculoMapper.toResponseDTO(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoDTO dto) {
        Veiculo veiculo = buscarPorId(id);

        if (!veiculo.getPlaca().equals(dto.getPlaca()) && veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RegraDeNegocioException("Placa ja cadastrada: " + dto.getPlaca());
        }

        veiculo.setPlaca(dto.getPlaca());
        veiculo.setAno(dto.getAno());
        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setCor(dto.getCor());
        veiculo.setCliente(buscarCliente(dto.getClienteId()));

        return veiculoMapper.toResponseDTO(veiculoRepository.save(veiculo));
    }

    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscar(Long id) {
        return veiculoMapper.toResponseDTO(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarTodos() {
        return veiculoRepository.findAll()
                .stream()
                .map(veiculoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deletar(Long id) {
        veiculoRepository.delete(buscarPorId(id));
    }

    private Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Veiculo nao encontrado com id: " + id));
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cliente nao encontrado com id: " + id));
    }
}
