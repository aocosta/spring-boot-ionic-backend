package com.alexcosta.cursomc.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alexcosta.cursomc.dto.CredenciaisDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

// Intercepta a requisição de login (/login)
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;
    
    private JWTUtil jwtUtil;

    // Construtor com argumentos
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
    	setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
	
    // Método para autenticar o login (email e senha)
	@Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
		// boilerplate
		try {
			// Instancia um CredenciaisDTO a partir dos dados de usuário e senha que vieram na requisição
			CredenciaisDTO creds = new ObjectMapper()
	                .readValue(req.getInputStream(), CredenciaisDTO.class);
	
	        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha(), new ArrayList<>());
	        
	        Authentication auth = authenticationManager.authenticate(authToken);
	        return auth;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Executa esse método em caso de sucesso na autenticação
	@Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
		// boilerplate
		String username = ((UserSS) auth.getPrincipal()).getUsername();
        String token = jwtUtil.generateToken(username);
        res.addHeader("Authorization", "Bearer " + token);
	}
	
	private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {
		
	    @Override
	    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
	            throws IOException, ServletException {
	        response.setStatus(401);
	        response.setContentType("application/json"); 
	        response.getWriter().append(json());
	    }
	    
	    private String json() {
	        long date = new Date().getTime();
	        return "{\"timestamp\": " + date + ", "
	            + "\"status\": 401, "
	            + "\"error\": \"Não autorizado\", "
	            + "\"message\": \"Email ou senha inválidos\", "
	            + "\"path\": \"/login\"}";
	    }
	}

}
