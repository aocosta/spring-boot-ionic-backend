package com.alexcosta.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexcosta.cursomc.domain.ItemPedido;
import com.alexcosta.cursomc.domain.Pagamento;
import com.alexcosta.cursomc.domain.PagamentoComBoleto;
import com.alexcosta.cursomc.domain.Pedido;
import com.alexcosta.cursomc.domain.enums.EstadoPagamento;
import com.alexcosta.cursomc.repositories.ItemPedidoRepository;
import com.alexcosta.cursomc.repositories.PagamentoRepository;
import com.alexcosta.cursomc.repositories.PedidoRepository;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	PagamentoRepository pagamentoRepository;
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	ItemPedidoRepository itemPedidoRepository;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = repository.findById(id);
		// return obj.orElse(null);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
	@Transactional
	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());

		Pagamento pagamento = pedido.getPagamento();
		pagamento.setPedido(pedido);
		pagamento.setEstado(EstadoPagamento.PENDENTE);
		if (pagamento instanceof PagamentoComBoleto) {
			boletoService.preencherPagamentoComBoleto((PagamentoComBoleto)pagamento, pedido.getInstante());
		}
		
		pedido = repository.save(pedido);
		pagamentoRepository.save(pagamento);
		
		for (ItemPedido ip : pedido.getItens()) {
			ip.setDesconto(0.0);
			ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(pedido);
		}
		
		itemPedidoRepository.saveAll(pedido.getItens());
		
		return pedido;
	}

}
