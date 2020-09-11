package com.alexcosta.cursomc.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private JWTUtil jwtUtil;
	private UserDetailsService userDetailsService;
	
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserDetailsService userDetailsService) {
		super(authenticationManager);
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}
	
	// Excecuta antes da requisição continuar
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain) throws IOException, ServletException {
		
		// Obtem o valor da variável Authorization que veio no Header da requisição
		String header = request.getHeader("Authorization");
		
		// Procedimento para liberar o usuário que está tendando acessar um endpoint
		
		// Verifica se a variável começa com a palavra Bearer
		if (header != null && header.startsWith("Bearer ")) {
			
			// Extrai o token da variável (ou seja, tudo que vem depois da palavra Bearer)
			UsernamePasswordAuthenticationToken auth = getAuthentication(header.substring(7));
			
			// Se o token existir
			if (auth != null) {
				// Libera o acesso
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		
		// Continua a requisição
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		// Se o token for válido
		if (jwtUtil.tokenValido(token)) {
			// Extrai o Username no token
			String username = jwtUtil.getUsername(token);
			// Busca o usuário no banco
			UserDetails user = userDetailsService.loadUserByUsername(username);
			// Retorna o tipo requerido pelo Spring Security
			return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}
		return null;
	}

}
