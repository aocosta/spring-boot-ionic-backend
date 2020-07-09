package com.alexcosta.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.alexcosta.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);

}
