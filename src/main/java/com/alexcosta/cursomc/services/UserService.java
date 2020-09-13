package com.alexcosta.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.alexcosta.cursomc.security.UserSS;

public class UserService {

	// Obtem o usuário logado
	public static UserSS authenticated() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		catch (Exception e) {
			return null;
		}
	}
}
