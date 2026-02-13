package com.nery.java_agendamento.controller;

import com.nery.java_agendamento.dto.AgendamentoRequestDTO;
import com.nery.java_agendamento.model.Agendamento;
import com.nery.java_agendamento.service.AgendamentoService;
import com.nery.java_agendamento.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*") // Libera acesso para o Front-end
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // LISTAR: Retorna todos os agendamentos
    @GetMapping
    public java.util.List<Agendamento> listar() {
        return agendamentoRepository.findAll();
    }

    // CRIAR: Recebe o pedido, valida e salva
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody AgendamentoRequestDTO dados) {
        try {
            Agendamento novoAgendamento = agendamentoService.criarAgendamento(dados);
            return ResponseEntity.ok(novoAgendamento);
        } catch (RuntimeException e) {
            // Se der erro (ex: horário ocupado), retorna erro 400 com a mensagem
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // CANCELAR: Recebe o ID e cancela
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            agendamentoService.cancelarAgendamento(id);
            return ResponseEntity.ok("Agendamento cancelado com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}