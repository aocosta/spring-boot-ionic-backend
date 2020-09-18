package com.alexcosta.cursomc.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.domain.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	// Não precisa implementar o método. O Spring detecta automáticamente
	@Transactional(readOnly = true)		// Sem transação de banco (lock) pra ficar mais rápido
	Page<Pedido> findByCliente(Cliente cliente, Pageable pageRequest);
}
