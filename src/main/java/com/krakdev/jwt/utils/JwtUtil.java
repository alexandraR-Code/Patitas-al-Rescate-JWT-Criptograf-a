package com.krakdev.jwt.utils;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

	private static final String SECRET = "PatitasAlRescate-ClaveLocal-2026-32Caracteres";

	private static final long EXPIRATION_TIME = 30 * 60 * 1000;

	private static final Algorithm ALGORITHT = Algorithm.HMAC256(SECRET);

	// Gnerar el token JWT
	public static String generarToken(String username, String rol) {
		Date ahora = new Date();
		Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

		return JWT.create().withSubject(username).withClaim("rol", rol).withIssuedAt(ahora).withExpiresAt(expiracion)
				.sign(ALGORITHT);
	}

	// Validra la firma y la expiracion del token
	public static DecodedJWT validarToken(String token) throws JWTVerificationException {
		return JWT.require(ALGORITHT).build().verify(token);

	}

}
