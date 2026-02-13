package com.nery.java_agendamento.config;

import com.nery.java_agendamento.model.Agendamento;
import com.nery.java_agendamento.model.Servico;
import com.nery.java_agendamento.model.Usuario;
import com.nery.java_agendamento.repository.AgendamentoRepository;
import com.nery.java_agendamento.repository.ServicoRepository;
import com.nery.java_agendamento.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(UsuarioRepository usuarioRepository,
                                      ServicoRepository servicoRepository,
                                      AgendamentoRepository agendamentoRepository) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                System.out.println(">>> A iniciar carregamento de dados essenciais...");

                // 1. Criar Apenas os Barbeiros
                Usuario beatriz = criarUsuario("Beatriz Lima", "beatriz@barbearia.com", Usuario.TipoUsuario.BARBEIRO);
                Usuario leandro = criarUsuario("Leandro Lima", "leandro@barbearia.com", Usuario.TipoUsuario.BARBEIRO);

                usuarioRepository.saveAll(Arrays.asList(beatriz, leandro));

                // 2. Criar Serviços
                Servico s1 = criarServico("Corte Social", "35.00", 30);
                Servico s2 = criarServico("Barba Modelada", "25.00", 20);
                Servico s3 = criarServico("Cabelo + Barba", "55.00", 50);
                Servico s4 = criarServico("Pezinho / Sobrancelha", "15.00", 15);

                servicoRepository.saveAll(Arrays.asList(s1, s2, s3, s4));

                // Nota: Não criamos clientes nem agendamentos fictícios.
                // A agenda começará vazia e pronta para uso real.

                System.out.println(">>> Barbeiros e Serviços carregados com sucesso!");
            }
        };
    }

    // Métodos auxiliares
    private Usuario criarUsuario(String nome, String email, Usuario.TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha("123"); // Senha padrão para testes
        u.setTipo(tipo);
        return u;
    }

    private Servico criarServico(String nome, String preco, int duracao) {
        Servico s = new Servico();
        s.setNome(nome);
        s.setPreco(new BigDecimal(preco));
        s.setDuracaoMinutos(duracao);
        return s;
    }
}