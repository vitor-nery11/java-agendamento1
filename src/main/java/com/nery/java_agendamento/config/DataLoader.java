package com.nery.java_agendamento.config;

import com.nery.java_agendamento.model.Servico;
import com.nery.java_agendamento.model.Usuario;
import com.nery.java_agendamento.repository.ServicoRepository;
import com.nery.java_agendamento.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(UsuarioRepository usuarioRepository, ServicoRepository servicoRepository) {
        return args -> {
            // Verifica se já existem dados para não duplicar
            if (usuarioRepository.count() == 0) {

                // 1. Criar um Barbeiro
                Usuario barbeiro = new Usuario();
                barbeiro.setNome("Carlos Barbeiro");
                barbeiro.setEmail("carlos@barbearia.com");
                barbeiro.setSenha("123456"); // Em prod, usaríamos BCrypt
                barbeiro.setTipo(Usuario.TipoUsuario.BARBEIRO);
                usuarioRepository.save(barbeiro);
                System.out.println(">>> Barbeiro criado: " + barbeiro.getNome());

                // 2. Criar um Cliente de teste
                Usuario cliente = new Usuario();
                cliente.setNome("João Cliente");
                cliente.setEmail("joao@gmail.com");
                cliente.setSenha("123456");
                cliente.setTipo(Usuario.TipoUsuario.CLIENTE);
                usuarioRepository.save(cliente);

                // 3. Criar Serviços
                Servico corte = new Servico();
                corte.setNome("Corte Social");
                corte.setPreco(new BigDecimal("35.00"));
                corte.setDuracaoMinutos(30);

                Servico barba = new Servico();
                barba.setNome("Barba Modelada");
                barba.setPreco(new BigDecimal("25.00"));
                barba.setDuracaoMinutos(20);

                servicoRepository.saveAll(Arrays.asList(corte, barba));
                System.out.println(">>> Serviços criados.");
            }
        };
    }
}
