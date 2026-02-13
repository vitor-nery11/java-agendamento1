package com.nery.java_agendamento.controller;

import com.nery.java_agendamento.dto.LoginRequestDTO;
import com.nery.java_agendamento.model.Usuario;
import com.nery.java_agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginData) {
        // 1. Buscar usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginData.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // 2. Verificar a senha (num sistema real, usaríamos BCrypt aqui)
            if (usuario.getSenha().equals(loginData.getSenha())) {
                // Sucesso! Retorna o usuário (sem a senha por segurança)
                usuario.setSenha(null);
                return ResponseEntity.ok(usuario);
            }
        }

        return ResponseEntity.status(401).body("Email ou senha inválidos.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario novoUsuario) {
        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Este email já está cadastrado.");
        }

        // Define o tipo padrão como CLIENTE se não vier preenchido
        if (novoUsuario.getTipo() == null) {
            novoUsuario.setTipo(Usuario.TipoUsuario.CLIENTE);
        }

        Usuario salvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok(salvo);
    }
}