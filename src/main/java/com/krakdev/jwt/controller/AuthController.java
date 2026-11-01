package com.krakdev.jwt.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakdev.jwt.entidades.Usuario;
import com.krakdev.jwt.services.UsuaioServices;
import com.krakdev.jwt.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuaioServices services;

	public AuthController(UsuaioServices services) {
		super();
		this.services = services;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioNuevo = services.registar(usuario);
			// usuarioNuevo.setPassword(null);
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
			String token = JwtUtil.generarToken(usuarioAutenticado.getUsername(), usuarioAutenticado.getRol());
			// No devolver la contrasena en la respuesta

			// usuarioAutenticado.setPassword(null);
			return ResponseEntity.ok(java.util.Map.of("token", token, "tipo", "Bearer", "username",
					usuarioAutenticado.getUsername(), "rol", usuarioAutenticado.getRol()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contrasena incorrecta");
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(@RequestHeader(value = "Authorization", required = false) String authorization) {

		try {
			// Verificar que exista la cabecera de Bearer
			if (authorization == null || !authorization.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado");
			}
			// Extraer el token de la caebcera
			String token = authorization.substring(7);

			// Validar la firma y la ecpiracion
			com.auth0.jwt.interfaces.DecodedJWT jwt = JwtUtil.validarToken(token);

			// Obtener los datos del usuario desde el toekn validado
			String username = jwt.getSubject();
			String rol = jwt.getClaim("rol").asString();

			// Saludar segun el rol
			String mensaje;

			if ("ADMIN".equals(rol)) {
				mensaje = "Bienvenido voluntario " + username + ". Tienes acceso como administrador.";
			} else if ("USER".equals(rol)) {
				mensaje = "Bienvenido adoptante " + username + ". Puedes consultar tu perfil.";
			} else {
				mensaje = "Bienvenido " + username;
			}
			return ResponseEntity.ok(java.util.Map.of("username", username, "rol", rol, "mensaje", mensaje));

		} catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido o expirado");

		}

	}

}
