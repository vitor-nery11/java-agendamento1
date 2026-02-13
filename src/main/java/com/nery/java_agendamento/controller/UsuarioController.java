package com.nery.java_agendamento.controller;

import com.nery.java_agendamento.model.Usuario;
import com.nery.java_agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listarBarbeiros() {
        // Busca todos os usuários e filtra apenas quem é BARBEIRO
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getTipo() == Usuario.TipoUsuario.BARBEIRO)
                .collect(Collectors.toList());
    }
}