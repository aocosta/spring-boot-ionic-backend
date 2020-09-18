package com.alexcosta.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.domain.ItemPedido;
import com.alexcosta.cursomc.domain.Pagamento;
import com.alexcosta.cursomc.domain.PagamentoComBoleto;
import com.alexcosta.cursomc.domain.Pedido;
import com.alexcosta.cursomc.domain.enums.EstadoPagamento;
import com.alexcosta.cursomc.repositories.ItemPedidoRepository;
import com.alexcosta.cursomc.repositories.PagamentoRepository;
import com.alexcosta.cursomc.repositories.PedidoRepository;
import com.alexcosta.cursomc.security.UserSS;
import com.alexcosta.cursomc.services.exceptions.AuthorizationException;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	// Injeta o componente @Bean emailService da classe de configuração (TestConfig ou DevConfig)
	@Autowired
	private EmailService emailService;

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

		// Precisa popular o objeto cliente para ter o nome dele no serviço de email
		pedido.setCliente(clienteService.find(pedido.getCliente().getId()));

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

			// Precisa popular o objeto produto para ter o nome dele no serviço de email
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(pedido);
		}
		
		itemPedidoRepository.saveAll(pedido.getItens());
		
		// System.out.println(pedido.toString());
		// emailService.sendOrderConfirmationEmail(pedido);
		emailService.sendOrderConfirmationHtmlEmail(pedido);
		
		return pedido;
	}
	
	// Método de busca de pedidos de um cliente de forma paginada
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		// Obtem o usuário logado
		UserSS user = UserService.authenticated();
		
		// Se não achou nenhum usuário logado
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		// Cria objeto de paginação
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		// Obtem o cliente
		Cliente cliente =  clienteService.find(user.getId());
		
		// Retorna busca paginada
		return repository.findByCliente(cliente, pageRequest);
	}

}
