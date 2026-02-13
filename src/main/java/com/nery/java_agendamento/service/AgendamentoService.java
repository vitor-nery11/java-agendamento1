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

    // Configurações de Horário
    private static final int HORARIO_ABERTURA = 9;
    private static final int HORARIO_FECHAMENTO = 19;
    private static final int HORARIO_ALMOCO_INICIO = 12; // Pausa
    private static final int HORARIO_ALMOCO_FIM = 13;    // Retorno

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    public Agendamento criarAgendamento(AgendamentoRequestDTO dados) {
        LocalDateTime dataInicio = dados.getDataHora();
        LocalDateTime agora = LocalDateTime.now();

        // 1. Validação: Não pode agendar no passado
        if (dataInicio.isBefore(agora)) {
            throw new RuntimeException("Erro: Você não pode agendar para um horário que já passou.");
        }

        // 2. Validação: Antecedência Mínima (ex: 30 minutos)
        // Evita que o cliente agende para "agora mesmo" de surpresa
        if (dataInicio.isBefore(agora.plusMinutes(30))) {
            throw new RuntimeException("Erro: O agendamento deve ser feito com no mínimo 30 minutos de antecedência.");
        }

        // 3. Validação de Horário Comercial e Almoço
        validarHorarioComercial(dataInicio);

        // 4. Buscar entidades
        Usuario cliente = usuarioRepository.findById(dados.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Usuario barbeiro = usuarioRepository.findById(dados.getBarbeiroId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        Servico servico = servicoRepository.findById(dados.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // 5. Calcular horário de término
        LocalDateTime dataFim = dataInicio.plusMinutes(servico.getDuracaoMinutos());

        // 6. Validação de Encerramento
        // Garante que o serviço TERMINA antes da barbearia fechar
        // Ex: Se fecha 19h, não pode começar um corte de 30min às 18:40
        if (dataFim.getHour() > HORARIO_FECHAMENTO ||
                (dataFim.getHour() == HORARIO_FECHAMENTO && dataFim.getMinute() > 0)) {
            throw new RuntimeException("O serviço ultrapassa o horário de fechamento (" + HORARIO_FECHAMENTO + "h).");
        }

        // 7. Validação de Conflito de Almoço (Serviço não pode invadir o almoço)
        // Se o serviço começa antes do almoço e termina depois do início do almoço
        if (dataInicio.getHour() < HORARIO_ALMOCO_INICIO && dataFim.getHour() >= HORARIO_ALMOCO_INICIO) {
            if (dataFim.getHour() > HORARIO_ALMOCO_INICIO || dataFim.getMinute() > 0) {
                throw new RuntimeException("O serviço conflita com o horário de almoço da equipe (12h-13h).");
            }
        }

        // 8. Verificar conflito com outros agendamentos
        List<Agendamento> conflitos = agendamentoRepository.findConflitos(
                barbeiro.getId(), dataInicio, dataFim);

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Este horário já está ocupado para o barbeiro " + barbeiro.getNome());
        }

        // 9. Salvar
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(dataInicio);
        agendamento.setDataHoraFim(dataFim);
        agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);

        return agendamentoRepository.save(agendamento);
    }

    public void cancelarAgendamento(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (agendamento.getStatus() == Agendamento.StatusAgendamento.CANCELADO) {
            throw new RuntimeException("Este agendamento já se encontra cancelado.");
        }

        if (agendamento.getDataHoraInicio().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RuntimeException("O cancelamento só é permitido com no mínimo 2 horas de antecedência.");
        }

        agendamento.setStatus(Agendamento.StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    private void validarHorarioComercial(LocalDateTime dataHora) {
        int hora = dataHora.getHour();

        // Regra 1: Horário de Funcionamento
        if (hora < HORARIO_ABERTURA || hora >= HORARIO_FECHAMENTO) {
            throw new RuntimeException("A barbearia funciona apenas das " + HORARIO_ABERTURA + "h às " + HORARIO_FECHAMENTO + "h.");
        }

        // Regra 2: Horário de Almoço
        if (hora >= HORARIO_ALMOCO_INICIO && hora < HORARIO_ALMOCO_FIM) {
            throw new RuntimeException("A barbearia fecha para almoço das " + HORARIO_ALMOCO_INICIO + "h às " + HORARIO_ALMOCO_FIM + "h.");
        }

        // Regra 3: Domingos
        if (dataHora.getDayOfWeek().getValue() == 7) {
            throw new RuntimeException("A barbearia não abre aos domingos.");
        }
    }
}