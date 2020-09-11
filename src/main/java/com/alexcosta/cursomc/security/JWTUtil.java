package com.alexcosta.cursomc.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

	// Obtem o valor da variável declarada no application.properties
	@Value("${jwt.secret}")
	private String secret;

	// Obtem o valor da variável declarada no application.properties
	@Value("${jwt.expiration}")
	private Long expiration;

	// Gerador de token
	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}

	// Validador de token
	public boolean tokenValido(String token) {
		// Obtem as reivindicações do token (usuário e tempo de expiração)
		Claims claims = getClaims(token);
		
		// Se conseguiu recuperar as reivindicação do token
		if (claims != null) {
			// Extrai o usuário
			String username = claims.getSubject();
			// Extrai a data de expiração
			Date expirationDate = claims.getExpiration();
			// Obtem a data atual
			Date now = new Date(System.currentTimeMillis());
			// Verifica se as reivindicações existem e se a data atual é anterior a data de expiração do token
			if (username != null && expirationDate != null && now.before(expirationDate)) {
				// Token valido
				return true;
			}
		}
		
		// Token inválido
		return false;
	}

	// Obtem as reivindicações do token
	private Claims getClaims(String token) {
		try {
			// Recupera as reivindicações a partir do token
			return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
		}
		catch (Exception e) {
			// No caso de não ser um token válido ou algum outro problema
			return null;
		}
	}

	// Obtem o usuário no token
	public String getUsername(String token) {
		// Obtem as reivindicações do token (usuário e tempo de expiração)
		Claims claims = getClaims(token);
		
		// Se conseguiu recuperar as reivindicação do token
		if (claims != null) {
			// Extrai o usuário do token e o retorna
			return claims.getSubject();
		}
		
		// Não foi possível recuperar as reivindicação do token
		return null;
	}

}