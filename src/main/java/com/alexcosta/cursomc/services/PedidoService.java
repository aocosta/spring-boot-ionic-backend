package com.alexcosta.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexcosta.cursomc.domain.Categoria;
import com.alexcosta.cursomc.domain.Pedido;
import com.alexcosta.cursomc.repositories.PedidoRepository;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repository;

	public Pedido buscar(Integer id) {
		Optional<Pedido> obj = repository.findById(id);
		// return obj.orElse(null);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}
}
