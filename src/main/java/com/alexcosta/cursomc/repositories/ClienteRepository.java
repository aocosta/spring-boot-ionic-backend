package com.alexcosta.cursomc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alexcosta.cursomc.domain.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{
	
	// Não precisa implementar o método. O Spring detecta automáticamente a busca por email
	@Transactional(readOnly = true)		// Sem transação de banco (lock) pra ficar mais rápido
	Cliente findByEmail(String email);
}
