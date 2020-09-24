package com.alexcosta.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.repositories.ClienteRepository;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	// Classe utilitária do java que gera valores aleatórios
	private Random random = new Random();

	public void sendNewPassword(String email) {
		
		// Busca o cliente através do email
		Cliente cliente = clienteRepository.findByEmail(email);
		
		// Se o cliente não existir
		if (cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		// Obtem uma nova senha aleatória
		String newPass = newPassword();
		
		// Seta a nova senha do cliente
		cliente.setSenha(bCryptPasswordEncoder.encode(newPass));
		
		// Salva o cliente
		clienteRepository.save(cliente);
		
		// Envia para o cliente sua nova senha por email
		emailService.sendNewPasswordEmail(cliente, newPass);
		
	}

	// Cria uma nova senha
	private String newPassword() {
		char[] vet = new char[10];
		for (int i = 0; i < 10; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	private char randomChar() {
		
		// Gera um número inteiro aleatório entre 0 e 2
		int opt = random.nextInt(3);
		
		if (opt == 0) {
			// Gera um caracter unicode correspondente a um dígito entre 0 e 9
			return (char) (random.nextInt(10) + 48);
		}
		else if (opt == 1) {
			// Gera um caracter unicode correspondente a uma letra maiúscula entre A e Z
			return (char) (random.nextInt(26) + 65);
		}
		else {
			// Gera um caracter unicode correspondente a uma letra minúsula entre a e z
			return (char) (random.nextInt(26) + 97);			
		}
	}
}
