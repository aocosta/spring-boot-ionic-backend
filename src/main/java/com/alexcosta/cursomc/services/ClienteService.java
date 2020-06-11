package com.alexcosta.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.dto.ClienteDTO;
import com.alexcosta.cursomc.repositories.ClienteRepository;
import com.alexcosta.cursomc.services.exceptions.DataIntegrityException;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public List<Cliente> findAll() {
		List<Cliente> clientes = repository.findAll();
		return clientes;
	}

	public Cliente update(Cliente updateCliente) {
		Cliente cliente = find(updateCliente.getId());
		updateData(cliente, updateCliente);
		return repository.save(cliente);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir o cliente porque há entidades relacionadas");
		}
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO clienteDto) {
		return new Cliente(clienteDto.getId(), clienteDto.getNome(), clienteDto.getEmail(), null, null);
	}

	private void updateData(Cliente cliente, Cliente updateCliente) {
		cliente.setNome(updateCliente.getNome());
		cliente.setEmail(updateCliente.getEmail());
	}

}
