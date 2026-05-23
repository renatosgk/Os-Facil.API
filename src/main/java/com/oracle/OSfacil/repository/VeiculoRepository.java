package com.oracle.OSfacil.repository;

import com.oracle.OSfacil.model.Veiculo;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    boolean existsByPlaca(String placa);
    List<Veiculo> findByClienteId(Long clienteId);
}
