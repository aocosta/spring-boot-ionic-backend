package com.alexcosta.cursomc.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alexcosta.cursomc.domain.Categoria;
import com.alexcosta.cursomc.domain.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	/*
	@Transactional(readOnly = true)		// Não precisa de transação de banco (lock) porque é só uma consulta
	@Query("SELECT DISTINCT obj FROM Produto obj INNER JOIN obj.categorias cat WHERE obj.nome LIKE %:nome% AND cat IN :categorias")
	Page<Produto> search(@Param("nome") String nome, @Param("categorias") List<Categoria> categorias, Pageable pageRequest);
	*/
	/*
	Mesma consulta acima usando padrões de nome do Spring Data
	@Transactional(readOnly = true)		// Não precisa de transação de banco (lock) porque é só uma consulta
	Page<Produto> findDistinctByNomeContainingAndCategoriasIn(String nome, List<Categoria> categorias, Pageable pageRequest);
	*/

	// Mesma consulta mas sobrepondo a query gerada pelo Spring Data no uso do padrões de nome
	@Transactional(readOnly = true)		// Não precisa de transação de banco (lock) porque é só uma consulta
	@Query("SELECT DISTINCT obj FROM Produto obj INNER JOIN obj.categorias cat WHERE obj.nome LIKE %:nome% AND cat IN :categorias")
	Page<Produto> findDistinctByNomeContainingAndCategoriasIn(@Param("nome") String nome, @Param("categorias") List<Categoria> categorias, Pageable pageRequest);
	
}
