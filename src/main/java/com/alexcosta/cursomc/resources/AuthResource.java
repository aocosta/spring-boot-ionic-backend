package com.alexcosta.cursomc.resources;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alexcosta.cursomc.dto.EmailDTO;
import com.alexcosta.cursomc.security.JWTUtil;
import com.alexcosta.cursomc.security.UserSS;
import com.alexcosta.cursomc.services.AuthService;
import com.alexcosta.cursomc.services.UserService;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private AuthService authService;

	// Dá um refresh no token do usuário
	@RequestMapping(value = "/refresh_token", method = RequestMethod.POST)
	public ResponseEntity<Void> refreshToken(HttpServletResponse response) {
		
		// Obtem o usuário logado
		UserSS user = UserService.authenticated();
		
		// Gera um token para o usuário
		String token = jwtUtil.generateToken(user.getUsername());
		
		// Seta o valor do token na variável Authorization do Header
		response.addHeader("Authorization", "Bearer " + token);
		
		// Expõe a autorização
		response.addHeader("access-control-expose-headers", "Authorization");
		
		// Retorna código de status 204
		return ResponseEntity.noContent().build();
	}

	// @Valid - realiza as validações que estão no DTO
	// @RequestBody - indica que o DTO vem no body do POST
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public ResponseEntity<Void> forgot(@Valid @RequestBody EmailDTO emailDTO) {
		
		// Gera um novo email e envia para o cliente
		authService.sendNewPassword(emailDTO.getEmail());
		
		// Retorna código de status 204
		return ResponseEntity.noContent().build();
	}
}
