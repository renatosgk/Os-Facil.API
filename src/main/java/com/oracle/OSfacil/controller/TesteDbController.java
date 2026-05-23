package com.oracle.OSfacil.controller;

import com.oracle.OSfacil.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testes-db")
public class TesteDbController {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/validar-senha")
    public ResponseEntity<String> testarSenha(@RequestParam String senha) {
        String resultado = repository.validarComplexidadeSenha(senha);
        return ResponseEntity.ok("Resposta do Oracle (fn_validar_senha_complexa): " + resultado);
    }

    @PostMapping("/relatorio-os-json")
    public ResponseEntity<String> rodarRelatorioJson() {
        try {
            jdbcTemplate.execute("BEGIN pr_relatorio_os_json; END;");
            return ResponseEntity.ok("Procedure pr_relatorio_os_json disparada! Confira a aba 'Saída DBMS' no Oracle.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao rodar relatório: " + e.getMessage());
        }
    }

    @PostMapping("/comparativo-salarial")
    public ResponseEntity<String> rodarComparativoSalarial() {
        try {
            repository.executarComparativoSalarial();
            return ResponseEntity.ok("Procedure pr_comparativo_salarial disparada via Repository! Confira o console do banco.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao rodar comparativo: " + e.getMessage());
        }
    }

    @PutMapping("/testar-trigger-auditoria/{id}")
    public ResponseEntity<String> testarTrigger(@PathVariable Long id, @RequestParam Double novoValor) {
        try {
            jdbcTemplate.update("UPDATE tb_ordemservico SET valor = ? WHERE id = ?", novoValor, id);
            return ResponseEntity.ok("Valor atualizado via Java! Verifique a tabela tb_auditoria_osFacil no Oracle para ver o log do Trigger.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao testar trigger: " + e.getMessage());
        }
    }
}
