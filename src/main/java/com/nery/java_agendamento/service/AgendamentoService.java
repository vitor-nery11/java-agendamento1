package com.nery.java_agendamento.service;

import com.nery.java_agendamento.dto.AgendamentoRequestDTO;
import com.nery.java_agendamento.model.Agendamento;
import com.nery.java_agendamento.model.Servico;
import com.nery.java_agendamento.model.Usuario;
import com.nery.java_agendamento.repository.AgendamentoRepository;
import com.nery.java_agendamento.repository.ServicoRepository;
import com.nery.java_agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    public Agendamento criarAgendamento(AgendamentoRequestDTO dados) {
        // 1. Buscar as entidades no banco
        Usuario cliente = usuarioRepository.findById(dados.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Usuario barbeiro = usuarioRepository.findById(dados.getBarbeiroId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        Servico servico = servicoRepository.findById(dados.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // 2. Calcular horário de término
        LocalDateTime dataInicio = dados.getDataHora();
        LocalDateTime dataFim = dataInicio.plusMinutes(servico.getDuracaoMinutos());

        // 3. Validação: Verificar conflito de horário
        List<Agendamento> conflitos = agendamentoRepository.findConflitos(
                barbeiro.getId(), dataInicio, dataFim);

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário indisponível para este barbeiro!");
        }

        // 4. Salvar
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(dataInicio);
        agendamento.setDataHoraFim(dataFim);
        agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);

        return agendamentoRepository.save(agendamento);
    }
}

