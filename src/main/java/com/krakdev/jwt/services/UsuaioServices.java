package com.krakdev.jwt.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krakdev.jwt.entidades.Usuario;
import com.krakdev.jwt.repository.UsuarioRepository;

@Service
public class UsuaioServices {
	private final UsuarioRepository usuarioRepository;

	public UsuaioServices(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario registar(Usuario usuario) {
		if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
			throw new IllegalArgumentException("El nombre del usuario ya esta registrado");
		}
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> login(String username, String password) {
		Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
		if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
			return usuario;
		}
		return Optional.empty();
	}

}
