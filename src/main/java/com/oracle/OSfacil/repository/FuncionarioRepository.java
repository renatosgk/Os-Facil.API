package com.oracle.OSfacil.repository;

import com.oracle.OSfacil.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    Optional<Funcionario> findByEmailIgnoreCase(String email);

    @Procedure(procedureName = "pr_comparativo_salarial")
    void executarComparativoSalarial();
    @Query(value = "SELECT fn_validar_senha_complexa(:senha) FROM DUAL", nativeQuery = true)
    String validarComplexidadeSenha(@Param("senha") String senha);
}
