package com.alexcosta.cursomc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.alexcosta.cursomc.security.JWTAuthenticationFilter;
import com.alexcosta.cursomc.security.JWTAuthorizationFilter;
import com.alexcosta.cursomc.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)	// permite autorização para perfis específicos
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	// O Spring injeta a implementação ao invés da interface
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JWTUtil jwtUtil;

	private static final String[] PUBLIC_MATCHERS = { "/h2-console/**" };

	private static final String[] PUBLIC_MATCHERS_GET = { "/produtos/**", "/categorias/**", "/estados/**" };

	// private static final String[] PUBLIC_MATCHERS_POST = { "/clientes/**", "/auth/forgot/**" };
	
	// Libera provisoriamente o endpoint de clientes para testes de upload de arquivos para o s3
	// private static final String[] PUBLIC_MATCHERS_POST = { "/clientes", "/clientes/picture", "/auth/forgot/**" };
	private static final String[] PUBLIC_MATCHERS_POST = { "/clientes", "/auth/forgot/**" };

	// Configura o que pode ser acessado pelo HTTP
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Libera o acesso ao h2 no caso de ambiente de teste
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		// Libera o acesso a aplicação por múltiplas fontes (ambiente de teste, aplicações front-end, etc...)
		// e desabilita proteção de ataque CSRF em sistemas stateless
		http.cors().and().csrf().disable();
		
		/*
		 *  Permite acesso aos métodos GET para todos os endpoints informados nos vetores PUBLIC_MATCHERS_GET,
		 *  Permite acesso a método POST para todos os endpoints informados nos vetores PUBLIC_MATCHERS_POST,
		 *  Permite acesso para todos os endpoints informados nos vetores PUBLIC_MATCHERS,
		 *  Para todo o resto exige autenticação
		*/
		http.authorizeRequests().
			antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll().
			antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll().
			antMatchers(PUBLIC_MATCHERS).permitAll().
			anyRequest().authenticated();
		
		// Registra permissão para os filtros de autenticação e autorização
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
		http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService));
		
		// Não permite criação de seção de usuário
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	// Informa para o Spring Security quem é o Serviço de Usuário e o método de encriptar senha
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
	
	// Configuração para acesso a aplicação por múltiplas fontes (ambiente de teste, aplicações front-end, etc...)
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	// Método para encriptar senhas
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
