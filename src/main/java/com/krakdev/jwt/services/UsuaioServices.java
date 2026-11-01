package com.krakdev.jwt.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
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
		// Generar el hash con la contrasena
		String passwordHash = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
		// Guardar el hash no la contrasena original
		usuario.setPassword(passwordHash);
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> login(String username, String password) {
		Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
		if (usuario.isPresent() && usuario.get().getPassword().equals(password)) { // compara con equals
			return usuario;
		}
		if (usuario.isPresent() && BCrypt.checkpw(password, usuario.get().getPassword())) { // compara directamnete con
																							// metodo de Bcrypt
			return usuario;
		}
		return Optional.empty();
	}

}
