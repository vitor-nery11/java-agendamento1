package com.nery.java_agendamento.controller;

import com.nery.java_agendamento.dto.AgendamentoRequestDTO;
import com.nery.java_agendamento.model.Agendamento;
import com.nery.java_agendamento.repository.AgendamentoRepository;
import com.nery.java_agendamento.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*") // Permite Frontend acessar
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // 🔹 Criar agendamento
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody AgendamentoRequestDTO dados) {
        try {
            Agendamento novoAgendamento = agendamentoService.criarAgendamento(dados);
            return ResponseEntity.ok(novoAgendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔹 Listar todos os agendamentos
    @GetMapping
    public List<Agendamento> listar() {
        return agendamentoRepository.findAll();
    }
}
