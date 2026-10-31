package com.krakdev.jwt.controller;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakdev.jwt.entidades.Usuario;
import com.krakdev.jwt.services.UsuaioServices;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuaioServices services;

	public AuthController(UsuaioServices services) {
		super();
		this.services = services;
	}

	@PostMapping("/registar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioNuevo = services.registar(usuario);
			usuarioNuevo.setPassword(null);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario datosLogin) {
		Optional<Usuario> usuario = services.login(datosLogin.getUsername(), datosLogin.getPassword());
		if (usuario.isPresent()) {
			Usuario usuarioAutenticado = usuario.get();
			// No devolver la contrasena en la respuesta
			usuarioAutenticado.setPassword(null);
			return ResponseEntity.ok(usuarioAutenticado);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contrasena incorrecta");
	}

}
