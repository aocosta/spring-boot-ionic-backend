package com.alexcosta.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.dto.ClienteDTO;
import com.alexcosta.cursomc.repositories.ClienteRepository;
import com.alexcosta.cursomc.resources.exception.FieldMessage;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO> {
	
	@Autowired
	private HttpServletRequest request;		// classe que permite recuperar as informações passadas na requisição
	
	@Autowired
	private ClienteRepository repository;
	
	@Override
	public void initialize(ClienteUpdate ann) {
	}

	@Override
	public boolean isValid(ClienteDTO objDto, ConstraintValidatorContext context) {
		
		// Recupera as informações passadas na requisição
		@SuppressWarnings("unchecked")		// suprime o erro Warning (amarelo) gerado ao se fazer o casting
		Map<String, String> requestMap = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		// Pega o id do cliente passado na requisição
		Integer id = Integer.parseInt(requestMap.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		Cliente cliente = repository.findByEmail(objDto.getEmail());
		if (cliente != null && !cliente.getId().equals(id)) {
			list.add(new FieldMessage("email", "Email já existente"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		
		return list.isEmpty();
	}
}